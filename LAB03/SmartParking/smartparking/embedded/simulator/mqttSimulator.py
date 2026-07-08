import tkinter as tk
from tkinter import ttk, scrolledtext
import paho.mqtt.client as mqtt
import ssl
import json
import threading
import random
from datetime import datetime, timezone

# ===========================================
# MQTT CONFIG
# ===========================================
BROKER = "4629b95c886c4677911c2126077efb80.s2.eu.hivemq.cloud"
PORT = 8883
USERNAME = "ESP32-CAM"
PASSWORD = "Davide00"

# ===========================================
# UTILITY
# ===========================================
def get_timestamp():
    """Timestamp UTC conforme ISO8601"""
    return datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")

def log(msg):
    """Scrive messaggi nel box di log"""
    log_box.insert(tk.END, msg + "\n")
    log_box.see(tk.END)

def send_message(topic, payload):
    """Invia un messaggio MQTT"""
    message = json.dumps(payload)
    result = client.publish(topic, message)
    if result.rc == mqtt.MQTT_ERR_SUCCESS:
        log(f"[{datetime.now().strftime('%H:%M:%S')}] Sent to {topic}: {message}")
    else:
        log(f"[ERROR] Failed to send message to {topic}")

# ===========================================
# MQTT CALLBACKS
# ===========================================
def on_connect(client, userdata, flags, reason_code, properties=None):
    if reason_code == 0:
        log("[INFO] Connected to HiveMQ Cloud ✅")
    else:
        log(f"[ERROR] Connection failed (code {reason_code}) ❌")

def on_publish(client, userdata, mid, reason_code=None, properties=None):
    log(f"[INFO] Message published (mid={mid})")

# ===========================================
# SIMULATION FUNCTIONS
# ===========================================
def simulate_coil():
    payload = {
        "deviceId": f"esp32-coil-{coil_position.get()}",
        "timeStamp": get_timestamp(),
        "coilAt": coil_position.get(),
        "status": coil_status.get()
    }
    send_message("parking/sensor/coildetection", payload)

def simulate_help():
    payload = {
        "timeStamp": get_timestamp(),
        "deviceId": f"esp32-help-{help_location.get()}",
        "location": help_location.get(),
        "value": "pressed"
    }
    send_message("parking/sensor/helpbutton", payload)

def simulate_nfc():
    payload = {
        "timeStamp": get_timestamp(),
        "date": datetime.now(timezone.utc).strftime("%Y-%m-%d"),
        "deviceId": "esp32-nfc-01",
        "nfcData": nfc_data.get()
    }
    send_message("parking/sensor/nfc", payload)

def simulate_plate():
    payload = {
        "timeStamp": get_timestamp(),
        "date": datetime.now(timezone.utc).strftime("%Y-%m-%d"),
        "deviceId": f"esp32-{camera_location.get()}",
        "cameraAt": camera_location.get(),
        "licensePlate": plate_number.get()
    }
    send_message("parking/sensor/platedetection", payload)

def simulate_status():
    payload = {
        "deviceId": status_device.get(),
        "status": status_value.get()
    }
    send_message(status_topic.get(), payload)

# === NUOVO: Keyboard Plate Insertion ===
def simulate_keyboard_insertion():
    payload = {
        "deviceId": "keyboard",
        "plate": keyboard_plate.get()
    }
    send_message("parking/sensor/keyboardInsertion", payload)

# ===========================================
# STATUS PERIODICO
# ===========================================
sensors = [
    {"deviceId": "esp32-coil-entry", "topic": "parking/sensor/entrycoil/status"},
    {"deviceId": "esp32-coil-exit", "topic": "parking/sensor/exitcoil/status"},
    {"deviceId": "esp32-help-entry", "topic": "parking/sensor/helpbutton/status"},
    {"deviceId": "esp32-help-exit", "topic": "parking/sensor/helpbutton/status"},
    {"deviceId": "esp32-nfc-01", "topic": "parking/sensor/nfc/status"},
    {"deviceId": "esp32-cameraIn", "topic": "parking/sensor/cameraIn/status"},
    {"deviceId": "esp32-cameraOut", "topic": "parking/sensor/cameraOut/status"},
]

auto_update_enabled = True

def send_periodic_status():
    if not auto_update_enabled:
        return
    for s in sensors:
        status = random.choices(["available", "unavailable"], weights=[0.85, 0.15])[0]
        payload = {
            "deviceId": s["deviceId"],
            "status": status
        }
        send_message(s["topic"], payload)
    threading.Timer(10.0, send_periodic_status).start()

# ===========================================
# INTERFACCIA GRAFICA
# ===========================================
root = tk.Tk()
root.title("SmartParking Embedded Simulator")
root.geometry("780x670")
root.resizable(False, False)

tab_control = ttk.Notebook(root)

# --- COIL DETECTION ---
tab_coil = ttk.Frame(tab_control)
ttk.Label(tab_coil, text="Coil position (entry/exit):").grid(column=0, row=0, padx=10, pady=10)
coil_position = ttk.Combobox(tab_coil, values=["entry", "exit"], width=10)
coil_position.current(0)
coil_position.grid(column=1, row=0)

ttk.Label(tab_coil, text="Status:").grid(column=0, row=1, padx=10)
coil_status = ttk.Combobox(tab_coil, values=["occupied", "free"], width=10)
coil_status.current(0)
coil_status.grid(column=1, row=1)

ttk.Button(tab_coil, text="Send Coil Event", command=simulate_coil).grid(column=0, row=2, columnspan=2, pady=10)

# --- HELP BUTTON ---
tab_help = ttk.Frame(tab_control)
ttk.Label(tab_help, text="Help Button location:").grid(column=0, row=0, padx=10, pady=10)
help_location = ttk.Combobox(tab_help, values=["entry", "exit"], width=10)
help_location.current(0)
help_location.grid(column=1, row=0)
ttk.Button(tab_help, text="Send HelpButton Event", command=simulate_help).grid(column=0, row=1, columnspan=2, pady=10)

# --- NFC READER ---
tab_nfc = ttk.Frame(tab_control)
ttk.Label(tab_nfc, text="NFC Tag Data:").grid(column=0, row=0, padx=10, pady=10)
nfc_data = tk.StringVar(value="04AABB22FF11")
ttk.Entry(tab_nfc, textvariable=nfc_data, width=25).grid(column=1, row=0)
ttk.Button(tab_nfc, text="Send NFC Event", command=simulate_nfc).grid(column=0, row=1, columnspan=2, pady=10)

# --- PLATE DETECTION ---
tab_plate = ttk.Frame(tab_control)
ttk.Label(tab_plate, text="Camera location:").grid(column=0, row=0, padx=10, pady=10)
camera_location = ttk.Combobox(tab_plate, values=["cameraIn", "cameraOut"], width=12)
camera_location.current(0)
camera_location.grid(column=1, row=0)
ttk.Label(tab_plate, text="License Plate:").grid(column=0, row=1, padx=10, pady=10)
plate_number = tk.StringVar(value="AB123CD")
ttk.Entry(tab_plate, textvariable=plate_number, width=20).grid(column=1, row=1)
ttk.Button(tab_plate, text="Send Plate Detection", command=simulate_plate).grid(column=0, row=2, columnspan=2, pady=10)

# --- DEVICE STATUS ---
tab_status = ttk.Frame(tab_control)
ttk.Label(tab_status, text="Topic:").grid(column=0, row=0, padx=10, pady=10)
status_topic = ttk.Combobox(tab_status, values=[
    "parking/sensor/nfc/status",
    "parking/sensor/entrycoil/status",
    "parking/sensor/exitcoil/status",
    "parking/sensor/cameraIn/status",
    "parking/sensor/cameraOut/status"
], width=40)
status_topic.current(0)
status_topic.grid(column=1, row=0)
ttk.Label(tab_status, text="Device ID:").grid(column=0, row=1, padx=10, pady=10)
status_device = tk.StringVar(value="entry_sensor_01")
ttk.Entry(tab_status, textvariable=status_device, width=25).grid(column=1, row=1)
ttk.Label(tab_status, text="Status:").grid(column=0, row=2, padx=10, pady=10)
status_value = ttk.Combobox(tab_status, values=["available", "unavailable"], width=15)
status_value.current(0)
status_value.grid(column=1, row=2)
ttk.Button(tab_status, text="Send Status Event", command=simulate_status).grid(column=0, row=3, columnspan=2, pady=10)

# --- NUOVA SEZIONE: KEYBOARD PLATE INSERTION ---
tab_keyboard = ttk.Frame(tab_control)
ttk.Label(tab_keyboard, text="License Plate (manual entry):").grid(column=0, row=0, padx=10, pady=10)
keyboard_plate = tk.StringVar(value="ZZ999ZZ")
ttk.Entry(tab_keyboard, textvariable=keyboard_plate, width=20).grid(column=1, row=0)
ttk.Button(tab_keyboard, text="Send Keyboard Insertion", command=simulate_keyboard_insertion).grid(column=0, row=1, columnspan=2, pady=10)

# --- ADD ALL TABS ---
tab_control.add(tab_coil, text="Coil Detection")
tab_control.add(tab_help, text="Help Button")
tab_control.add(tab_nfc, text="NFC Reader")
tab_control.add(tab_plate, text="Plate Detection")
tab_control.add(tab_status, text="Device Status")
tab_control.add(tab_keyboard, text="Keyboard Plate Insertion")
tab_control.pack(expand=1, fill="both")

# --- LOG BOX ---
log_box = scrolledtext.ScrolledText(root, width=95, height=15)
log_box.pack(padx=10, pady=10)

# ===========================================
# MQTT CONNECTION
# ===========================================
client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
client.username_pw_set(USERNAME, PASSWORD)
client.tls_set(cert_reqs=ssl.CERT_NONE)
client.tls_insecure_set(True)
client.on_connect = on_connect
client.on_publish = on_publish

try:
    client.connect(BROKER, PORT, 60)
    client.loop_start()
except Exception as e:
    log(f"[ERROR] Cannot connect: {e}")

# ===========================================
# AUTO STATUS START
# ===========================================
send_periodic_status()

root.mainloop()
