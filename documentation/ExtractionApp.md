# ExtractionApp - Android Image Processing with OpenCV

## 📌 Overview

**ExtractionApp** is an Android application designed for image processing using OpenCV. It allows users to:

- Select an input image from the gallery.
- Optionally select a background image for reference.
- Choose an image size (S, M, L, etc.).
- Adjust image processing parameters (tolerance, brightness, edge detection thresholds, contour color).
- Process the image using OpenCV and display the result.

This app replicates the functionality of a Python script for image manipulation, now implemented in Java for Android.

---

## 📂 Project Structure

```
ExtractionApp/
│-- app/                    # Main Android App Module
│   │-- src/main/java/       # Java Code
│   │-- src/main/res/        # UI Layouts and Resources
│   │-- AndroidManifest.xml  # App Configuration
│
│-- openCV/                 # OpenCV Module (Imported SDK)
│   │-- sdk/                # OpenCV SDK for Android
│   │-- java/               # OpenCV Java Bindings
│
│-- gradle/                 # Build Configuration
│-- README.md               # This Documentation
```
 
 
---

## 🛠️ Installation & Setup

### 1️⃣ Prerequisites

Ensure you have the following installed:

- **Android Studio** (Latest version)
- **Android SDK & NDK**
- **OpenCV SDK for Android**

### 2️⃣ Clone the Repository

```bash
git clone https://github.com/yourusername/ExtractionApp.git
cd ExtractionApp
```

### 3️⃣ Download & Extract OpenCV SDK

1. **Download the latest OpenCV Android SDK** from [the OpenCV website](https://opencv.org/releases/).
2. Extract it into the **root of your project**:
   ```bash
   mkdir -p OpenCV-android-sdk/
   unzip opencv-*.zip -d OpenCV-android-sdk/
   ```
3. Move the SDK contents into the project’s OpenCV module:
   ```bash
   mv OpenCV-android-sdk/sdk ExtractionApp/openCV/
   ```

### 4️⃣ Import OpenCV as a Module

1. **Open Android Studio**.
2. Navigate to `File` → `New` → `Import Module`.
3. Select `ExtractionApp/openCV/` as the module to import.
4. Rename the module name from `:sdk` to `openCV`.
5. Click **Finish** and allow Gradle to sync.

### 5️⃣ Configure OpenCV in Gradle

#### **Modify `settings.gradle` to include OpenCV:**

```gradle
include ':openCV'
project(':openCV').projectDir = new File('openCV')
```

#### **Modify `app/build.gradle` to include OpenCV:**

```gradle
dependencies {
    implementation project(':openCV')
}
```

---

## 6️⃣ Run the App

1. **Build the project**
``` 
cd ExtractionApp
./gradlew clean
./gradlew assembleDebug --warning-mode all

```


2. **Connect an Android device** OR **Start an emulator**.
3. Click **Run (▶)** in Android Studio.
4. Open **Logcat** and check for OpenCV initialization messages.

---

## 📸 Usage Guide

### **1️⃣ Select an Image**
- Click **"Select Image"** to choose a file from the gallery.
- The image will be displayed in the preview area.

### **2️⃣ (Optional) Select a Background**
- Click **"Select Background (Optional)"** to choose a reference background image.

### **3️⃣ Adjust Image Processing Settings**
- **Size:** Choose from Small (S), Medium (M), Large (L), etc.
- **Tolerance:** Adjust the color tolerance for background removal.
- **Brightness:** Modify brightness level.
- **Edge Detection:** Set Threshold1 and Threshold2.

### **4️⃣ Process Image**
- Click **"Process Image"** to apply OpenCV transformations.
- The processed images will be displayed : image with transparent background , image with edge .

---

 