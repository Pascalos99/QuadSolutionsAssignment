cd ../quad-assignment-backend
if not exist target/QuadAssignment.jar call mvnw clean package
cmd /K java -jar target/QuadAssignment.jar