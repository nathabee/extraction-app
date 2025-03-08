# Table of Contents
**(Will be generated automatically later)**

---

# Presentation
This document explains how the **ExtractionApp** Android application was created on **Ubuntu**, including setting up **OpenCV 4.11.0** for Android development using Android Studio.

---

# 1. Installation: GitHub Repository

We first create a **GitHub local directory** where our project will reside.
In this example, we will use:

```
/home/nathalie/coding/extraction-app
```

Personally, I like to include:
- `README.md` (Project description)
- `.gitignore` (To exclude unnecessary files from Git tracking)
- `github hook` (For automation if needed)
- `documentation/WORKLOG.md` (To track development progress)

---

# 2. Installation: Android Studio

### **Installing Android Studio on Ubuntu**
Android Studio is the official **IDE for Android development**. To install it on Ubuntu:

1. Download the latest **Android Studio** from [the official website](https://developer.android.com/studio).
2. Extract it to a preferred location, e.g.:
   ```bash
   sudo tar -xvf android-studio-*.tar.gz -C /opt/
   ```
3. Add an alias in `~/.bashrc` for quick access:
   ```bash
   echo 'export PATH=$PATH:/opt/android-studio/bin' >> ~/.bashrc
   source ~/.bashrc
   ```
4. Start Android Studio with:
   ```bash
   android-studio
   ```

Follow the on-screen setup to configure **SDK, Emulator, and necessary dependencies**.

---

# 3. Installation: OpenCV
 
**1️⃣ Download the OpenCV Android SDK**
- Go to the official [OpenCV releases page](https://opencv.org/releases/).
- Download the **latest OpenCV Android SDK** (`opencv-4.x-android-sdk.zip`).
- Extract it to a location like:
  ```bash
  /home/nathalie/coding/extraction-app/OpenCV-android-sdk/

   ```


# 4. Android App Project Creation

For more details, refer to: [OpenCV Android Development Guide](https://docs.opencv.org/4.x/d5/df8/tutorial_dev_with_OCV_on_Android.html)

### **1. Create the Android SDK Project**
1. Start **Android Studio**.
2. Create a new project with **"Empty View Activity"**.
3. Choose **Java** as the programming language.
4. Set the project details:
   - **Name:** `ExtractionApp`
   - **Location:** `<your local GitHub directory>`
   - **Build System:** Groovy DSL

### **2.  Import OpenCV as a Module in Android Studio**
1. **Open Android Studio** and load your `ExtractionApp` project.
2. Navigate to **File** → **New** → **Import Module**.
3. **Select the OpenCV SDK directory**:
   ```
   /home/nathalie/coding/extraction-app/OpenCV-android-sdk/sdk
   ```
4. Click **Finish**.

**Note:** If **Finish is disabled** or there are Gradle errors, ensure that the `sdk` folder contains `build.gradle`.

### **3️⃣ Link OpenCV in Gradle**
- Open `settings.gradle` and add:
  ```groovy
  include ':opencv'
  project(':opencv').projectDir = new File('/home/nathalie/coding/extraction-app/OpenCV-android-sdk/sdk')
  ```
- Open `app/build.gradle` and add:
  ```groovy
  dependencies {
      implementation project(':opencv')
  }
  ```

### **4️⃣ Sync and Run**
- Click **Sync Now** in Android Studio.
- Open `MainActivity.java` and initialize OpenCV:
  ```java
  import org.opencv.android.OpenCVLoader;
  import android.util.Log;

  public class MainActivity extends AppCompatActivity {
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          if (!OpenCVLoader.initDebug()) {
              Log.e("OpenCV", "Initialization failed");
          } else {
              Log.d("OpenCV", "Initialization successful");
          }
      }
  }
  ```
- Run the app on an **emulator or physical Android device**.

---

---

# 5. Testing and Running the Application

### **1. Building the Project**
- In **Android Studio**, click **Build > Make Project** to compile your application.
- Resolve any errors or missing dependencies if they appear.

### **2. Running in an Android Emulator**
- Open **AVD Manager** in Android Studio.
- Create a new virtual device and select an Android version.
- Click **Run > Run 'app'** and select the emulator.

### **3. Running on a Real Android Device**
1. Connect your Android phone to Ubuntu via USB.
2. Enable **Developer Mode** and **USB Debugging** on the phone.
3. In a terminal, check if the device is recognized:
   ```bash
   adb devices
   ```
4. If recognized, run the app via Android Studio and select your device.

---

# 6. Conclusion
By following these steps, you have successfully built, tested, and run your OpenCV-powered Android application. 🚀

