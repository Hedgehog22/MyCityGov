# MyCityGov

## 1. Start MinIO

Run the following command in your terminal to start the MinIO container:

```bash
sudo docker run -p 9000:9000 -p 9001:9001 \
  -e "MINIO_ROOT_USER=changeme" \
  -e "MINIO_ROOT_PASSWORD=changeme" \
  minio/minio server /data --console-address ":9001"
```

---

## 2. Environment Variables (`.env` file)

Create a `.env` file with the following configuration:

```env
# --- Twilio SMS Configuration ---
TWILIO_SID=ACxxxxx
TWILIO_TOKEN=changeme
TWILIO_PHONE=+16183624308

# --- MinIO Configuration ---
MINIO_URL=http://localhost:9000
MINIO_ACCESS_KEY=changeme
MINIO_SECRET_KEY=changeme
MINIO_BUCKET=mycitygov-files
```

---

## 3. Start the Gov Identity Mock Service

Run the following command in the mock service project:

```bash
./mvn spring-boot:run
```

---

## 4. Start the Main Application

Build and start the main application:

```bash
./mvn clean install
./mvn spring-boot:run
```
