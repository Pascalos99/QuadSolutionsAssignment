#!/usr/bin/env bash
cd ../quad-assignment-backend
if [ ! -f target/QuadAssignment.jar ]; then
	./mvnw clean package
fi
java -jar target/QuadAssignment.jar