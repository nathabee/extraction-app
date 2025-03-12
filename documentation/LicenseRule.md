### **🔍 General Rule:**
When you **include** third-party libraries **with different licenses** in your project, your **own project's license must be at least as permissive as the most restrictive included license**. 

---

### **📜 Understanding How Licenses Work Together**
| License Type          | Usage Freedom | Attribution Needed | Modification Disclosure | Redistribution Rules |
|-----------------------|--------------|--------------------|------------------------|----------------------|
| **MIT** (Your Project) | ✅ Very Free | ✅ Yes | ❌ No | ✅ Yes, with attribution |
| **Apache 2.0** (OpenCV) | ✅ Free | ✅ Yes | ✅ Yes | ✅ Yes, with attribution |
| **SIL OFL** (Gentium Font) | ✅ Free | ✅ Yes | ✅ Yes (for font) | ❌ Cannot sell font alone |
| **BSD 2-Clause** (Glide) | ✅ Free | ✅ Yes | ❌ No | ✅ Yes, with attribution |

---

### **🚦 What Does This Mean for Your Project?**
1. **Can You Keep MIT?** ✅ **Yes, because MIT is permissive enough to work with all these licenses.**  
   - MIT **does not restrict** how you distribute, use, or modify your own code.
   - Since **Apache 2.0, BSD 2-Clause, and SIL OFL are all compatible** with MIT, you can **keep MIT as your project's license**.
  
2. **What Restrictions Do You Need to Follow?**
   - You must **include attribution and license files** for:
     - **OpenCV (Apache 2.0)** → Requires **attribution** and **modification disclosure**.
     - **Gentium Font (SIL OFL)** → Requires **attribution** and states you **cannot sell the font separately**.
     - **Glide (BSD 2-Clause)** → Requires **attribution**.

3. **Does Your License Affect These Third-Party Components?**  
   - **No!** The licenses of OpenCV, Glide, and the font remain the same.
   - Your project is **MIT-licensed**, but OpenCV **remains under Apache 2.0**, and **you cannot change that**.
   - The Gentium font **stays under SIL OFL**, and you **cannot sell it** separately.
