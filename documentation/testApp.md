Creating an Android app in **Java** with **Android Studio** that includes:
- A **custom app icon**  
- A **menu** with options to select parameters  
- **Different screens (Activities or Fragments) for different choices**  
- **Maintaining context between screens**  

### **1. Steps to Create the App**
1. **Set up your project** in Android Studio.
2. **Change the app icon.**
3. **Create a menu in the toolbar.**
4. **Implement multiple screens using Activities or Fragments.**
5. **Pass data between screens without losing context.**

---

### **2. Setting Up Your Project**
1. **Open Android Studio** → Select **New Project** → Choose **Empty Activity** → Name it.
2. **Set up dependencies** (default ones will be fine).

---

### **3. Change the App Icon**
- Replace `ic_launcher` in `res/mipmap/` with your custom image.
- Alternatively, go to **File → New → Image Asset** and select your icon.

---

### **4. Create a Menu in the Toolbar**
#### **A. Create the Menu XML File**
- Go to `res/menu/` (create `menu/` if it doesn't exist).
- Create a new file: `menu_main.xml`

```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/menu_settings"
        android:title="Settings"
        android:icon="@android:drawable/ic_menu_preferences"
        android:showAsAction="never" />
    <item
        android:id="@+id/menu_create"
        android:title="Create"
        android:icon="@android:drawable/ic_menu_add"
        android:showAsAction="never" />
    <item
        android:id="@+id/menu_manage"
        android:title="Manage"
        android:icon="@android:drawable/ic_menu_manage"
        android:showAsAction="never" />
</menu>
```

#### **B. Add Menu to MainActivity**
Modify `MainActivity.java`:

```java
package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_create:
                startActivity(new Intent(this, CreateActivity.class));
                return true;
            case R.id.menu_manage:
                startActivity(new Intent(this, ManageActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
```

---

### **5. Create Different Screens (Activities)**
For each option in the menu (`Settings`, `Create`, `Manage`), create new Activities:

#### **A. Create a New Activity**
1. **Right-click** on `app/java/com.example.myapp`
2. **New → Activity → Empty Activity**
3. **Name them:** `SettingsActivity`, `CreateActivity`, `ManageActivity`

Each new Activity should have a corresponding XML layout file:
- `activity_settings.xml`
- `activity_create.xml`
- `activity_manage.xml`

#### **B. Example Code for One Activity (SettingsActivity.java)**
```java
package com.example.myapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
```

---

### **6. Maintain Context Between Screens**
To **keep data when switching screens**, use one of these methods:

#### **A. Pass Data Using `Intent`**
Example: Sending data from `MainActivity` to `SettingsActivity`
```java
Intent intent = new Intent(this, SettingsActivity.class);
intent.putExtra("username", "JohnDoe");
startActivity(intent);
```

In `SettingsActivity.java`, retrieve it:
```java
String username = getIntent().getStringExtra("username");
```

#### **B. Use SharedPreferences (For Saving Data Across Sessions)**
In `MainActivity`:
```java
SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
SharedPreferences.Editor editor = prefs.edit();
editor.putString("username", "JohnDoe");
editor.apply();
```

In `SettingsActivity`:
```java
SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
String username = prefs.getString("username", "DefaultUser");
```

#### **C. Use a Singleton Class (For Global State)**
Create a `SessionManager.java`:
```java
package com.example.myapp;

public class SessionManager {
    private static SessionManager instance;
    private String username;

    private SessionManager() { }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
```

Usage in `MainActivity`:
```java
SessionManager.getInstance().setUsername("JohnDoe");
```

Retrieve it in any Activity:
```java
String username = SessionManager.getInstance().getUsername();
```

---

### **7. Running & Testing**
1. Run the app and test the menu.
2. Click a menu item to navigate to a different screen.
3. Pass data between screens and ensure it remains.

---

### **8. Summary**
✔ **Set up project and changed the app icon**  
✔ **Created a menu using XML and Java**  
✔ **Implemented multiple screens using Activities**  
✔ **Maintained context using Intents, SharedPreferences, or Singleton**  

Would you like me to generate a **ZIP file** with the full project code?