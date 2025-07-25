Here's a **combined and polished `README.md`** that you can paste directly into your GitHub repo. It includes both the **mobile (Android)** and **web dashboard** projects clearly separated with stylish Markdown formatting.

---

```markdown
# 🌍 QuakeApp – Earthquake Detection & Monitoring System

QuakeApp is a full-stack earthquake detection and monitoring system composed of:

- 📱 **Mobile App (Android)** — Detects seismic activity using onboard sensors and visualizes events on Google Maps.
- 🖥️ **Web Dashboard** — Monitors and analyzes real-time seismic data using a modern, responsive web interface.

---

## 🧭 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [📱 Mobile App (Android)](#-mobile-app-android)
- [🖥️ Web Dashboard (Next.js)](#-web-dashboard-nextjs)
- [🗂️ Project Structure](#️-project-structure)
- [🌐 Environment Variables](#-environment-variables)
- [📄 License](#-license)

---

## ✨ Features

- Earthquake detection using mobile sensors (accelerometer)
- Real-time alert system using WebSockets
- Location-aware notification broadcasting
- Seismic event visualization (maps & tables)
- Emergency safety center tools
- Google Maps & Leaflet integration
- Web dashboard for search, filter, and analysis

---

## 🏗️ Architecture

```

\[Android App] <---> \[Node.js Backend] <---> \[PostgreSQL]
|
\[Web Dashboard]

````

---

## 📱 Mobile App (Android)

### 🔧 Setup & Installation

1. Install **Android Studio** and ensure **Java 8+** and **SDK 24+** are available.
2. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/QuakeApp.git
````

3. Open the `app/` folder in Android Studio.
4. Add your **Google Maps API Key** in `app/src/main/AndroidManifest.xml`:

   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY_HERE" />
   ```
5. Connect a physical device or emulator, then click **Run**.

### 🔐 Permissions Required

* Location (fine & coarse)
* Network access
* Foreground service
* Notifications
* Contacts, SMS (for emergency features)
* Vibration, Wake lock

### 📱 Key Features

* Earthquake detection using the accelerometer
* Real-time WebSocket alerts
* Event feed and detail screens
* Safety center with check-in and emergency tools
* Map integration for visualizing quake zones

---

## 🖥️ Web Dashboard (Next.js)

A modern, responsive dashboard to monitor earthquakes in real-time.

### 🚀 Tech Stack

* **Framework**: Next.js (App Router)
* **Language**: TypeScript
* **Styling**: Tailwind CSS, PostCSS
* **UI**: Radix UI, shadcn/ui, Lucide Icons
* **Maps**: Leaflet + React-Leaflet
* **Forms**: React Hook Form + Zod
* **Charts**: Recharts

### 🔧 Installation

```bash
cd mobile
npm install
# or
yarn install
```

### 🧪 Run Locally

```bash
npm run dev
# or
yarn dev
```

Visit: [http://localhost:3000](http://localhost:3000)

### 💡 Dashboard Features

* Login (demo: password = `password123`)
* Realtime earthquake table & map
* Search, filter, paginate by location/magnitude
* Dark/light mode toggle
* Accessible design with toasts and UI feedback

---

## 🗂️ Project Structure

```
/
├── mobile/              # Web Dashboard (Next.js)
│   ├── app/             # App routes and layout
│   ├── components/      # UI & map/table components
│   ├── lib/             # Utils and sample data
│   └── public/          # Static assets
│
├── app/                 # Android App (Java)
│   ├── src/             
│   │   ├── main/java/com/aiquake/
│   │   ├── res/values/
│   │   └── AndroidManifest.xml
│
├── backend/             # Node.js Backend API
│   ├── server.js
│   ├── package.json
│   └── .env
```

---

## 🌐 Environment Variables

For the backend (`backend/.env`):

```
PORT=3000
DATABASE_URL=postgres://postgres:your_password@localhost:5432/quakeapp
```

Update API endpoints in:

* `mobile/app/dashboard/page.tsx`
* `backend/server.js` (for DB or alert settings)

---

## 📄 License

This project is licensed under the **MIT License**.
See [LICENSE](./LICENSE) for more details.

---

## 🙌 Acknowledgements

* [Next.js](https://nextjs.org/)
* [Leaflet](https://leafletjs.com/)
* [React Native](https://reactnative.dev/)
* [Tailwind CSS](https://tailwindcss.com/)
* [Radix UI](https://www.radix-ui.com/)
* [PostgreSQL](https://www.postgresql.org/)
* [Google Maps Platform](https://developers.google.com/maps)

---

> *Contributions welcome! Feel free to fork, star ⭐, or open issues/PRs.*
> Made with 💙 by Mohammed Kazaz

```
