CREATE TABLE driver (
    driver_id INTEGER PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INTEGER,
    has_license BOOLEAN
);

CREATE TABLE car (
    car_id INTEGER PRIMARY KEY,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255),
    cost DECIMAL(10, 2)
);

CREATE TABLE driverCar (
    person_id INTEGER,
    car_id INTEGER,
    PRIMARY KEY (person_id, car_id),
    FOREIGN KEY (person_id) REFERENCES Persons(person_id),
    FOREIGN KEY (car_id) REFERENCES Cars(car_id)
);