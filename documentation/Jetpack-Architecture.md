**# Jetpack Architecture Presentation**

## **Table of Contents**

1. Introduction
2. MVVM Architecture
3. LiveData
4. Navigation Component
5. Activity vs. Fragment
6. Jetpack Components in the Project
7. Jetpack vs. Other Architectures
8. Menu and Navigation
9. Passing Arguments from UI to ViewModel
10. compose or not compose

---

## **1. Introduction**

Jetpack was introduced by **Google in 2018** to modernize Android development by providing a set of **libraries, tools, and best practices**. It was designed to **simplify development**, **reduce boilerplate code**, and ensure **backward compatibility**. Before Jetpack, developers had to handle many low-level tasks manually, leading to **complex, error-prone code**.

Jetpack integrates seamlessly with **Kotlin** and introduces modern architectures like **MVVM**, **LiveData**, and **Navigation Component**, making apps more maintainable and efficient. 🚀

---

## **2. MVVM (Model-View-ViewModel) Architecture**

MVVM is a design pattern that separates UI logic from business logic, making the code **easier to test, scale, and maintain**.

### **Structure of MVVM**

| Layer         | Responsibility                        | Example in This Project                           |
| ------------- | ------------------------------------- | ------------------------------------------------- |
| **Model**     | Manages data & business logic         | `DataStoreManager.kt` (app settings)              |
| **View**      | Displays UI & listens to user input   | `SettingsFragment.kt`, `GalleryFragment.kt`       |
| **ViewModel** | Acts as a bridge between Model & View | `SettingsViewModel.kt` (handles LiveData updates) |

### **How MVVM Works**

- The **ViewModel** holds the UI logic independently from the UI.
- The **View** (Fragment) listens to **LiveData** from ViewModel.
- When **data changes**, ViewModel **notifies the UI automatically**.

---

## **3. LiveData**

LiveData is an observable data holder that updates the UI **automatically** when the data changes.

### **Why Use LiveData?**

- ✅ **No manual UI updates** → UI **listens to data changes**.
- ✅ **No memory leaks** → LiveData is **lifecycle-aware**.
- ✅ **Ensures real-time updates** between Fragments.

### **Example in Our Project**

In `SettingsViewModel.kt`, we store **imageSize** in **LiveData**:

```kotlin
val imageSize = dataStoreManager.imageSize.asLiveData()
```

Then, in **SettingsFragment**, we observe it:

```kotlin
viewModel.imageSize.observe(viewLifecycleOwner) { imageSize ->
    spinner.setSelection(sizes.indexOf(imageSize))
}
```

💡 **Whenever ****\`\`**** changes, LiveData automatically updates the spinner UI!** 🚀

---

## **4. Navigation Component**

Before Jetpack, **navigation between screens** was done manually using `Intent` (for Activities) or `FragmentTransaction`. Jetpack **Navigation Component** **simplifies** this by using a \*\*single \*\*\`\`.

### **Why Use Navigation Component?**

- ✅ **Handles Fragment navigation automatically**.
- ✅ **Manages back stack for you**.
- ✅ **Works with SafeArgs (type-safe argument passing between screens)**.

### **Example in Our Project**

Instead of using **manual Fragment transactions**, we define a **navigation graph** (`nav_graph.xml`):

```xml
<navigation app:startDestination="@id/homeFragment">
    <fragment android:id="@+id/homeFragment" android:name="com.example.visubee.ui.HomeFragment"/>
    <fragment android:id="@+id/settingsFragment" android:name="com.example.visubee.ui.SettingsFragment"/>
</navigation>
```

Now, we **navigate between fragments** using:

```kotlin
findNavController().navigate(R.id.settingsFragment)
```

💡 **No need for FragmentTransactions! Navigation is automatic and smooth.** 🚀

---

## **5. Activity vs. Fragment**

### **What is an Activity?**

An **Activity** represents a full-screen UI component that the user interacts with.

### **What is a Fragment?**

A **Fragment** is a modular **sub-section** of an Activity that can be reused inside different parts of the UI.

### **Why Does This Project Use Fragments Instead of Activities?**

- **Only one Activity (**\`\`**)** manages all screens, making memory usage efficient.
- **Navigation between screens** is handled automatically by `NavController`.
- **Shared UI elements** remain persistent without needing to recreate them.

---

## **6. Jetpack Components in the Project**

| Jetpack Component        | Where It's Used                                                        | Why We Use It                                             |
| ------------------------ | ---------------------------------------------------------------------- | --------------------------------------------------------- |
| **Navigation Component** | `MainActivity.kt`, `nav_graph.xml`                                     | Handles Fragment navigation efficiently.                  |
| **ViewModel**            | `SettingsViewModel.kt`                                                 | Stores and manages UI-related data across fragments.      |
| **LiveData**             | `SettingsViewModel.kt`, `SettingsFragment.kt`, `ProcessingFragment.kt` | Ensures UI updates automatically when data changes.       |
| **DataStore**            | `DataStoreManager.kt`                                                  | Saves settings persistently, replacing SharedPreferences. |

---

## **7. Jetpack vs. Other Architectures**

Jetpack’s **MVVM architecture**, **LiveData**, and **Navigation Component** have similar concepts in **modern web frameworks** like **Next.js/React, Django, and Angular**.

| Concept              | Jetpack (Android)            | Next.js / React     | Django                    | Angular                 |
| -------------------- | ---------------------------- | ------------------- | ------------------------- | ----------------------- |
| **State Management** | ViewModel + LiveData         | Redux / Context API | Django Context Processors | Angular Services + RxJS |
| **Navigation**       | Jetpack Navigation Component | React Router        | Django URL Routing        | Angular Router          |


| Concept | **Jetpack (Android)** | **Next.js / React** | **Django** | **Angular** |
|---------|----------------|----------------|----------------|----------------|
| **Model (Data Layer)** | `DataStoreManager.kt`, `Repository` (Handles data storage, API calls) | API calls (e.g., fetch/axios) or Context API/Redux for state management | Models + ORM (e.g., `User.objects.all()`) | Services (Injectable services, RxJS Observables) |
| **View (UI Layer)** | `Fragment` / `Activity` (Displays UI) | Components (`.tsx/.jsx` files) | Templates (`.html` + Django Template Language) | Components (`.component.html + .component.ts`) |
| **ViewModel (State Management)** | `ViewModel` (Holds UI logic, LiveData updates UI) | React Hooks (`useState`, `useEffect`) or Context API/Redux | Views + Class-based views (CBVs) | Angular Services (with RxJS for state updates) |
| **Navigation** | Jetpack Navigation Component (`NavController`) | React Router (`<BrowserRouter>`) | Django URL Routing (`urls.py`) | Angular Router (`RouterModule.forRoot([])`) |

---

---

## **8. Menu and Navigation**

### \*\*Difference Between \*\*`** and **`

| **Feature**                                 | **nav\_graph.xml** | **drawer\_menu.xml** |
| ------------------------------------------- | ------------------ | -------------------- |
| Defines which screens exist                 | ✅ Yes              | ❌ No                 |
| Handles UI elements (menu items)            | ❌ No               | ✅ Yes                |
| Manages back stack behavior                 | ✅ Yes              | ❌ No                 |
| Used for Navigation Drawer (Hamburger menu) | ❌ No               | ✅ Yes                |
| Used by NavController                       | ✅ Yes              | ✅ (indirectly)       |

---

## **9. Passing Arguments from UI to ViewModel**

Instead of directly passing parameters, the **ViewModel should own and manage state**.

### **Example**

```kotlin
btnProcess.setOnClickListener {
    viewModel.setThreshold1(threshold1SeekBar.progress.toFloat())
    viewModel.setThreshold2(threshold2SeekBar.progress.toFloat())
    viewModel.setTolerance(toleranceSeekBar.progress)
    viewModel.setBrightness(brightnessSeekBar.progress)
    viewModel.processImage()
}
```

By storing values in **LiveData**, the UI updates automatically when data changes.



---

## **10. compose or not compose**


**Jetpack Compose** is **not required**—it’s just a **newer way** to build UI in Android.  
 
✔ Jetpack Compose is **optional**, and XML **is still valid**.  
✔ If you ever want to **move to Compose**, you will need to rewrite your UI in Kotlin functions.  

---

### **✅ Are You Using Jetpack Correctly?**
1. **If your project is XML-based:**  
   - **Yes**, you are using Jetpack correctly!  
   - Jetpack is a collection of libraries (ViewModel, LiveData, DataStore, etc.), and XML is still part of Jetpack.  
   
2. **If you want to use Jetpack Compose:**  
   - You need to remove XML layouts and define UI in Kotlin instead.  
   - Your project doesn’t currently use Compose, so you don’t need to worry about it.

 
 
---

### **🚀 What You Keep & What You Remove When Switching to Compose**
| Resource Type           | Keep? | Why? |
|-------------------------|-------|------|
| **📂 res/layout/** (XML UI files) | ❌ **REMOVE** | Compose replaces all XML-based layouts with `@Composable` functions. |
| **📂 res/drawable/** (Icons, Shapes) | ✅ **KEEP** | You still need icons, vectors, and custom shapes for Compose UI. |
| **📂 res/values/strings.xml** (Text & Translations) | ✅ **KEEP** | Strings are still needed for localization & accessibility. |
| **📂 res/values/colors.xml** (Colors) | ✅ **KEEP** | You can still use `colors.xml`, or define colors in Kotlin with `Color()` objects. |
| **📂 res/values/dimens.xml** (Margins, Padding, Sizes) | ✅ **KEEP** (Optional) | Can still be used, but many define sizes directly in Compose. |
| **📂 res/values/styles.xml** (Themes) | ❌ **REMOVE** (or simplify) | Jetpack Compose has its own `MaterialTheme` system, replacing `styles.xml`. |
| **📂 res/navigation/** (Navigation Graph) | ❌ **REMOVE** | Compose has **NavHost** & `rememberNavController()` instead of XML-based navigation. |
| **📂 res/menu/** (Menus) | ❌ **REMOVE** | You create menus directly in Compose with `DropdownMenu`, `TopAppBar`, etc. |

---

### **✨ Summary: What Happens in a Full Compose App?**
✅ **You keep**:
- `strings.xml`
- `colors.xml`
- `dimens.xml` (if you want)
- `drawable/` resources (icons, vectors)

❌ **You remove**:
- `layout/` XML files → Everything is written in `@Composable` functions
- `navigation/` XML → Navigation is handled with Compose `NavHost`
- `menu/` XML → Menus are built directly in Kotlin
- `styles.xml` (partially) → Compose uses `MaterialTheme` instead

---

### **💡 Example: XML vs Compose UI**
#### **🔹 Old XML-based UI (`activity_main.xml`)**
```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView
        android:text="Hello XML!"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</LinearLayout>
```

#### **🔹 Jetpack Compose UI (`MainScreen.kt`)**
```kotlin
@Composable
fun MainScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "Hello Compose!", fontSize = 20.sp)
    }
}
```
 
---


# **Conclusion**

Jetpack simplifies Android development, making it **efficient, scalable, and modern**. Using **MVVM, LiveData, Navigation Component, and DataStore**, our project follows the best practices for Android development. 🚀

