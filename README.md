# PartTracker App

**PartTracker** is an Android application developed for tracking used parts in a manufacturing/assembly environment. This project was built as part of an internship at **Bajaj Auto Ltd., Akurdi**.

##  Features

-  Track usage of parts by model  
-  Sync data with Firebase Firestore  
-  View historical part usage  
-  Dashboard for model-wise part usage summary  
-  Offline-first capability with local Room database  
-  Real-time updates using snapshot listeners  

##  Tech Stack

- **Kotlin**
- **Android Jetpack Components** (ViewModel, LiveData, Room, Navigation)
- **Firebase Firestore**
- **Volley (HTTP client)**
- **MVVM Architecture**


## 📁 Project Structure

```
com.example.parttracker
├── data/            # Room DB and DAOs
├── firebase/        # Firestore sync manager
├── model/           # Data models
├── repository/      # Repositories
├── ui/              # Fragments and UI logic
├── viewmodel/       # ViewModels
```

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/PartTracker-App.git
   ```

2. Open in Android Studio

3. Connect to your Firebase project or replace `google-services.json`

##  Author

**Priya Jha**  
 priya18jha08@gmail.com  


---

_Developed as part of the Summer Internship Project at Bajaj Auto Ltd._
