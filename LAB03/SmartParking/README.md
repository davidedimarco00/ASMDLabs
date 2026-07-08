# SmartParking
Software to manage an automated parking lot.

TO START THE SOFTWARE:
- Install the requirements on the .../embedded/simulator (pip install -r requirements.txt)
- On the same folder run the simulator (py .\mqttSimulator.py)

- In the Backend folder run the folloqing command: 
    - gradle build
    - docker-compose up -d --build

- Install the requirements for the frontend : .../frontend (pip install -r requirements.txt)
- Launch the app.py from the frontend folder to run the API (Access to the frontend at the localhost:5002) [Username: ADMIN, Password: ADMIN]

- Import the file compass-connection.json stored in .../backend in Mongo Compass to access the view of the database.

- You are READY to start!

Requirements: 
- Java 17
- Gradle 8.13
- Python 3.12


