


#  Project presentation


This project follows **Jetpack's recommended MVVM (Model-View-ViewModel) architecture**. Let's go step by step.

---

## **1️⃣ Project Structure**
```
📦 KotlinTestApp
 ┣ 📂 app/src/main/java/com/example/kotlintestapp
 ┃ ┣ 📂 ui/ (Fragments for each screen)
 ┃ ┃ ┣ 📜 HomeFragment.kt
 ┃ ┃ ┣ 📜 ProcessingFragment.kt
 ┃ ┃ ┣ 📜 SettingsFragment.kt
 ┃ ┃ ┣ 📜 GalleryFragment.kt
 ┃ ┃ ┗ 📜 AboutFragment.kt
 ┃ ┣ 📂 viewmodel/ (Manages UI-related data)
 ┃ ┃ ┗ 📜 SettingsViewModel.kt
 ┃ ┣ 📂 data/ (Manages persistent data)
 ┃ ┃ ┗ 📜 DataStoreManager.kt
 ┃ ┗ 📜 MainActivity.kt
 ┣ 📂 res/layout/ (XML UI files for each Fragment)
 ┃ ┣ 📜 activity_main.xml
 ┃ ┣ 📜 fragment_processing.xml
 ┃ ┣ 📜 fragment_settings.xml
 ┃ ┣ 📜 fragment_gallery.xml
 ┃ ┗ 📜 fragment_about.xml
 ┣ 📂 res/navigation/
 ┃ ┗ 📜 nav_graph.xml
 ┣ 📜 build.gradle.kts (Project Configuration)
 ┗ 📜 libs.versions.toml (Centralized Dependencies)
```

---

## **2️⃣ File-by-File Explanation**
### **📌 MainActivity.kt**
🔹 **Role:**  
- Acts as the **only Activity** in the app.  
- Holds the **NavHostFragment**, which switches between different screens (`Fragments`).  
- Uses **Navigation Component** to move between fragments.

🔹 **Code Highlights:**
```kotlin
val navController = findNavController(R.id.nav_host_fragment)
setupActionBarWithNavController(navController, appBarConfiguration)
navView.setupWithNavController(navController)
```
💡 This ensures that clicking a menu item opens the correct screen.

---

### **📌 nav_graph.xml (Jetpack Navigation)**
🔹 **Role:**  
- Defines the **app's navigation structure**.
- Replaces `Intent` navigation (which is used in multi-Activity apps).

🔹 **Code Highlights:**
```xml
<navigation app:startDestination="@id/homeFragment">
    <fragment android:id="@+id/homeFragment" android:name="com.example.kotlintestapp.ui.HomeFragment"/>
    <fragment android:id="@+id/settingsFragment" android:name="com.example.kotlintestapp.ui.SettingsFragment"/>
</navigation>
```
💡 This makes it **easy to switch screens** without manually handling transactions.

---

### **📌 SettingsFragment.kt**
🔹 **Role:**  
- Allows the user to change `imageSize` (XS, S, M, L, XL) and `galleryPath`.  
- Uses `SettingsViewModel` to store data in **Jetpack DataStore**.

🔹 **Code Highlights:**
```kotlin
viewModel.imageSize.observe(viewLifecycleOwner) { imageSize ->
    spinner.setSelection(sizes.indexOf(imageSize))
}

viewModel.galleryPath.observe(viewLifecycleOwner) { galleryPath ->
    editTextPath.setText(galleryPath)
}
```
💡 The `ViewModel` updates the UI automatically whenever data changes.

---

### **📌 ProcessingFragment.kt**
🔹 **Role:**  
- Displays `imageSize`, which is retrieved from `SettingsViewModel`.  
- The user can change it **temporarily**, but it **does not affect** the app-wide setting.

🔹 **Code Highlights:**
```kotlin
viewModel.imageSize.observe(viewLifecycleOwner) { imageSize ->
    spinner.setSelection(sizes.indexOf(imageSize))
}
```
💡 This fragment **observes the same ViewModel**, ensuring **data consistency**.

---

### **📌 DataStoreManager.kt (Persistent Storage)**
🔹 **Role:**  
- Saves `imageSize` and `galleryPath` persistently using **Jetpack DataStore** (instead of SharedPreferences).  

🔹 **Code Highlights:**
```kotlin
val imageSize: Flow<String> = dataStore.data.map { preferences ->
    preferences[IMAGE_SIZE_KEY] ?: "M"
}

suspend fun saveSettings(imageSize: String, galleryPath: String) {
    dataStore.edit { preferences ->
        preferences[IMAGE_SIZE_KEY] = imageSize
        preferences[GALLERY_PATH_KEY] = galleryPath
    }
}
```
💡 This makes data **persist** across app restarts.

---

### **📌 SettingsViewModel.kt**
🔹 **Role:**  
- Acts as a **bridge between UI (Fragments) and DataStoreManager**.  
- Keeps data **live & synchronized** across different fragments.

🔹 **Code Highlights:**
```kotlin
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    val imageSize = dataStoreManager.imageSize.asLiveData()
    val galleryPath = dataStoreManager.galleryPath.asLiveData()

    fun saveSettings(imageSize: String, galleryPath: String) {
        viewModelScope.launch {
            dataStoreManager.saveSettings(imageSize, galleryPath)
        }
    }
}
```
💡 This ensures **real-time updates** between Fragments.

---

### **📌 GalleryFragment.kt**
🔹 **Role:**  
- Loads images from `galleryPath`.  
- Currently, the **image loading logic needs to be implemented**.

🔹 **Code Highlights:**
```kotlin
viewModel.galleryPath.observe(viewLifecycleOwner) { galleryPath ->
    // TODO: Load images dynamically from the selected galleryPath
}
```
💡 The gallery folder is **dynamically updated** based on settings.

---

### **📌 AboutFragment.kt**
🔹 **Role:**  
- Displays a **simple welcome message**.

🔹 **Code Highlights:**
```xml
<TextView android:text="Hello, welcome to my KotlinTestApp!" android:textSize="18sp"/>
```
💡 **No logic here**, just static UI.

---

## **3️⃣ 🔄 How Do These Files Work Together?**
### **💡 Data Flow**
1️⃣ **User opens SettingsFragment** → Changes `imageSize` → Presses **Save**  
2️⃣ **SettingsViewModel updates Jetpack DataStore** (data is now persistent).  
3️⃣ **ProcessingFragment & GalleryFragment automatically update** because they **observe LiveData**.  
4️⃣ **When the app restarts**, `imageSize` and `galleryPath` **persist**, thanks to **Jetpack DataStore**.

---

## **4️⃣ Summary (Why This Approach is Best)**
🔹 **MVVM Architecture** → Keeps UI separate from data logic.  
🔹 **Jetpack Navigation Component** → Makes screen transitions smoother.  
🔹 **Jetpack ViewModel + LiveData** → Ensures UI updates dynamically.  
🔹 **Jetpack DataStore** → Replaces `SharedPreferences` with modern storage.  

---

# how did we apply JetPack to this project

# What is jetpack?


**Jetpack** is a collection of modern Android libraries that make app development easier and more efficient. In this project, we are using **several Jetpack components**.

---

## **📌 What Jetpack Components Are Used in This Project?**
| Jetpack Component | Where It's Used | Why We Use It |
|-------------------|----------------|---------------|
| **Navigation Component** | `MainActivity.kt`, `nav_graph.xml` | Handles Fragment navigation efficiently. |
| **ViewModel** | `SettingsViewModel.kt` | Stores and manages UI-related data across fragments. |
| **LiveData** | `SettingsViewModel.kt`, `SettingsFragment.kt`, `ProcessingFragment.kt` | Ensures UI updates automatically when data changes. |
| **DataStore** | `DataStoreManager.kt` | Saves settings persistently, replacing SharedPreferences. |

---

### **🔹 1. Jetpack Navigation Component (Fragment Navigation)**
**What it does:**  
- Replaces `Intent`-based navigation between Activities.
- Allows **smooth transitions** between Fragments using `NavController`.

**Where it is used:**  
- `MainActivity.kt` contains `NavHostFragment` which switches between different **Fragments**.
- `nav_graph.xml` defines the **navigation flow**.

🔹 **Before Jetpack Navigation** (Manual way using `Intent`):
```kotlin
val intent = Intent(this, ProcessingActivity::class.java)
startActivity(intent)
```

✅ **With Jetpack Navigation Component** (Simpler & more efficient):
```kotlin
findNavController().navigate(R.id.processingFragment)
```

---

### **🔹 2. Jetpack ViewModel (State Management)**
**What it does:**  
- **Stores UI-related data** (e.g., `imageSize`, `galleryPath`).
- Survives **configuration changes** (e.g., screen rotation).
- Allows Fragments to **share data** without directly depending on each other.

**Where it is used:**  
- `SettingsViewModel.kt` stores `imageSize` and `galleryPath`.
- `SettingsFragment.kt` and `ProcessingFragment.kt` **observe** ViewModel.

✅ **Without ViewModel (Bad practice)**  
Each Fragment would **directly access data**, leading to potential bugs.

✅ **With Jetpack ViewModel (Best practice)**  
```kotlin
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    val imageSize = dataStoreManager.imageSize.asLiveData()
}
```
💡 Now **multiple fragments can observe** `imageSize`, and the UI **updates automatically**.

---

### **🔹 3. Jetpack LiveData (Auto-updating UI)**
**What it does:**  
- Ensures the UI **automatically updates** when data changes.
- Reduces the need for **manual UI updates**.

**Where it is used:**  
- In `SettingsFragment.kt` and `ProcessingFragment.kt` to **observe** `imageSize`.

✅ **Without LiveData (Manual UI update required)**  
```kotlin
val size = settingsViewModel.imageSize
spinner.setSelection(size)  // Must be called manually when data changes
```

✅ **With LiveData (UI updates automatically!)**
```kotlin
viewModel.imageSize.observe(viewLifecycleOwner) { imageSize ->
    spinner.setSelection(sizes.indexOf(imageSize)) // No need for manual calls!
}
```

💡 If `imageSize` is updated in **SettingsFragment**, the change **instantly reflects** in **ProcessingFragment**.

---

### **🔹 4. Jetpack DataStore (Better Persistent Storage)**
**What it does:**  
- Stores app settings **persistently**.
- **Replaces SharedPreferences** (which is slower and not recommended anymore).
- Uses **Kotlin Coroutines** to save and read data asynchronously.

**Where it is used:**  
- `DataStoreManager.kt` manages storage for `imageSize` and `galleryPath`.
- `SettingsViewModel.kt` interacts with DataStore.

✅ **Without DataStore (Old way using SharedPreferences)**
```kotlin
val sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
val editor = sharedPreferences.edit()
editor.putString("imageSize", "M")
editor.apply()
```

✅ **With Jetpack DataStore (Modern & Efficient)**
```kotlin
suspend fun saveSettings(imageSize: String, galleryPath: String) {
    dataStore.edit { preferences ->
        preferences[IMAGE_SIZE_KEY] = imageSize
    }
}
```
💡 **Benefits of DataStore:**
- ✅ **More efficient** (SharedPreferences blocks the main thread, DataStore doesn’t).
- ✅ **Uses Kotlin Coroutines** for smooth performance.
- ✅ **Safer & recommended** by Google.

---
 
# **Detailed Specification for Background Removal Implementation**
 
## **1️⃣ Overview of Functionality**
The background removal process should:
1. **Determine the background color**  
   - Use a **reference image** if provided.
   - Otherwise, extract the **top-left 10% region** of the input image and compute the dominant color.
2. **Create a mask**  
   - Convert the image to HSV color space.
   - Define a **tolerance range** around the dominant background color.
   - Generate a mask to identify background pixels.
3. **Apply transparency**  
   - Convert the original image to **RGBA**.
   - Replace background pixels with **transparent** ones (alpha = 0).

---

## **2️⃣ Inputs**
### **Required Inputs**
| Parameter         | Type        | Description |
|------------------|------------|-------------|
| `inputBitmap`    | `Bitmap`    | The input image from which the background needs to be removed. |
| `referenceBitmap` | `Bitmap?`   | An optional reference image to determine the background color. |
| `tolerance`      | `Int`       | The allowed deviation from the background color for background detection. Default: **30**. |
| `brightness`     | `Int`       | Minimum brightness for background color detection. Default: **50**. |

---

## **3️⃣ Background Color Extraction**
### **Case 1: Reference Image Provided**
- Extract the **dominant color** from the **reference image** using HSV color space.
- Compute the **hue range** using the tolerance.

### **Case 2: No Reference Image**
- Extract the **top-left 10% region** of the input image.
- Compute the **average HSV color** of this region.
- Define the **hue range** dynamically.

---

## **4️⃣ Background Removal Steps**
1. **Convert Input Image to HSV**  
   - Transform the image from **RGB to HSV** for better color-based segmentation.

2. **Create a Binary Mask**
   - Define the background color's **upper and lower HSV boundaries** using tolerance.
   - Use `inRange()` to create a **binary mask**, marking background pixels.

3. **Apply Mask to Original Image**
   - Convert input image to **RGBA** (add alpha channel).
   - Set detected background pixels as **fully transparent**.

---

## **5️⃣ Expected Output**
| Output Parameter       | Type      | Description |
|----------------------|----------|-------------|
| `transparentBitmap` | `Bitmap`  | Image with the background removed (transparent background). |

---