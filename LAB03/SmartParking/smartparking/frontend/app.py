from flask import Flask, render_template, jsonify, redirect, url_for, session, request
import requests

app = Flask(__name__)
app.secret_key = 'your-secret-key-here'

global token

def get_auth_token():
    gateway_url = "http://localhost:8080/auth/generate"
    params = {
        "clientId": "smartparking-client",
        "clientSecret": "Smart-Parking"
    }
    headers = {"Content-Type": "application/json"}

    try:
        response = requests.post(gateway_url, params=params, headers=headers)
        response.raise_for_status()
        return response.json().get("token")
    except requests.exceptions.RequestException as e:
        print(f"Errore generazione token: {e}")
        return None

# ===============================
# PAGINE FRONTEND
# ===============================
@app.route('/')
def home():
    return render_template('index.html', title="SmartParking Frontend")

@app.route('/login')
def login_page():
    return render_template('login.html', title="Login - SmartParking")

@app.route('/dashboard')
def dashboard():
    if 'username' not in session:
        return redirect(url_for('login_page'))
    return render_template('dashboard.html', title="SmartParking Dashboard")

@app.route('/analytics')
def analytics():
    if 'username' not in session:
        return redirect(url_for('login_page'))
    return render_template('analytics.html', title="SmartParking Analytics")

@app.route('/reportviewer')
def reportviewer():
    if 'username' not in session:
        return redirect(url_for('login_page'))
    return render_template('reportViewer.html', title="SmartParking Report Viewer")

@app.route('/parkmanagement')
def parkingstatus():
    if 'username' not in session:
        return redirect(url_for('login_page'))
    return render_template('embeddedStatus.html', title="SmartParking Status")

@app.route('/ticketsviewer')
def ticketsviewer():
    if 'username' not in session:
        return redirect(url_for('login_page'))
    return render_template('ticketsviewer.html', title="SmartParking Tickets Viewer")

# ===============================
# API AUTH ENDPOINTS
# ===============================
@app.route('/api/login', methods=['POST'])
def login():
    global token
    token = get_auth_token()
    print(token)
    if not token:
        return jsonify({"error": "Token not available"}), 401

    data = request.get_json()

    gateway_url = "http://localhost:8080/auth-service/login"
    headers = {
        "Content-Type": "application/json",
        "X-Auth-Token": token
    }

    try:
        response = requests.post(gateway_url, json=data, headers=headers)
        response.raise_for_status()
        result = response.json()
        session['username'] = result.get('username')
        return jsonify(result), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/login/<username>', methods=['GET'])
def verify_user(username):
    token = get_auth_token()
    if not token:
        return jsonify({"error": "Token not available"}), 401

    gateway_url = f"http://localhost:8080/auth-service/login/{username}"
    headers = {"X-Auth-Token": token}

    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/logout', methods=['POST'])
def logout():
    data = request.get_json(silent=True) or {}
    print(data)
    username = data.get('username') or session.get('username', 'ADMIN')

    print(username)

    gateway_url = "http://localhost:8080/auth-service/logout"
    headers = {
        "Content-Type": "application/json",
        "X-Auth-Token": token
    }

    try:
        response = requests.post(gateway_url, json=data, headers=headers)
        response.raise_for_status()
        session.pop('username', None)
        return jsonify({"message": "Logout done"}), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

# ===============================
# API ANALYTICS ENDPOINTS
# ===============================

@app.route('/api/analytics/report/<date>', methods=['GET'])
def generate_report(date):
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    gateway_url = f"http://localhost:8080/analytics-service/report/{date}"
    headers = {"X-Auth-Token": token}

    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/analytics/report/get/<date>', methods=['GET'])
def get_report_by_date(date):
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    gateway_url = f"http://localhost:8080/analytics-service/report/get/{date}"
    headers = {"X-Auth-Token": token}

    try:
        response = requests.get(gateway_url, headers=headers)
        if response.status_code == 404:
            return jsonify({"error": "Report non trovato"}), 404
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/analytics/reports', methods=['GET'])
def get_all_reports():
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    gateway_url = "http://localhost:8080/analytics-service/reports"
    headers = {"X-Auth-Token": token}

    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

# ===============================
# API PARKING ENDPOINTS
# ===============================
@app.route('/api/parkmanagement/slots', methods=['GET'])
def get_parking_slots():
    global token
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    gateway_url = "http://localhost:8080/parking-service/slots"
    headers = {"X-Auth-Token": token}
    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        raw = response.json()
        map_data = raw.get('map', {})
        total = map_data.get('totalSlots')
        available = map_data.get('availableSlots')
        occupied = map_data.get('occupiedSlots')
        return jsonify({
            "totalSlots": total,
            "availableSlots": available,
            "occupiedSlots": occupied
        }), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/parkmanagement/status', methods=['GET'])
def get_current_status():
    global token
    if 'username' not in session:
        return jsonify({"error": "Non autorizzato"}), 401

    gateway_url = "http://localhost:8080/parking-service/getEmbeddedStatus"
    headers = {"X-Auth-Token": token}
    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        raw = response.json()
        devices = raw.get('map', {}).get('devices', [])
        return jsonify({"devices": devices}), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/parkmanagement/openEntryBarrier', methods=['POST'])
def open_entry_barrier():
    global token
    if 'username' not in session:
        return jsonify({"error": "Non autorizzato"}), 401

    data = request.get_json() or {}
    gateway_url = "http://localhost:8080/parking-service/openEntryBarrier"
    headers = {"X-Auth-Token": token, "Content-Type": "application/json"}

    try:
        response = requests.post(gateway_url, json={}, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/parkmanagement/openExitBarrier', methods=['POST'])
def open_exit_barrier():
    global token
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    data = request.get_json() or {}
    gateway_url = "http://localhost:8080/parking-service/openExitBarrier"
    headers = {"X-Auth-Token": token, "Content-Type": "application/json"}

    try:
        response = requests.post(gateway_url, json={}, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/parkmanagement/addCar', methods=['POST'])
def add_car():
    global token
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    data = request.get_json() or {}
    gateway_url = "http://localhost:8080/parking-service/addCar"
    headers = {"X-Auth-Token": token, "Content-Type": "application/json"}

    try:
        response = requests.post(gateway_url, json=data, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/payment/setTariff', methods=['POST'])
def set_tariff():
    global token
    if 'username' not in session:
        return jsonify({"error": "Notautorized"}), 401

    data = request.get_json() or {}
    gateway_url = "http://localhost:8080/payment-service/setTariff"
    headers = {"X-Auth-Token": token, "Content-Type": "application/json"}

    try:
        response = requests.post(gateway_url, json=data, headers=headers)
        response.raise_for_status()
        try:
            return jsonify(response.json()), response.status_code
        except ValueError:
            return jsonify({"message": "Tariff updated"}), response.status_code
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

# ===============================
# API TICKETING ENDPOINTS
# ===============================
@app.route('/api/ticketing/getAllTickets', methods=['GET'])
def get_all_tickets():
    global token
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    gateway_url = "http://localhost:8080/ticketing-service/getAllTickets"
    headers = {"X-Auth-Token": token}

    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/ticketing/getAllHistoryTickets', methods=['GET'])
def get_all_history_tickets():
    global token
    if 'username' not in session:
        return jsonify({"error": "Not autorized"}), 401

    gateway_url = "http://localhost:8080/ticketing-service/getAllHistoryTickets"
    headers = {"X-Auth-Token": token}

    try:
        response = requests.get(gateway_url, headers=headers)
        response.raise_for_status()
        return jsonify(response.json()), 200
    except requests.exceptions.RequestException as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002, debug=True)
