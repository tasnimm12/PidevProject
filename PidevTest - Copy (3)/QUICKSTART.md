# Credix - Quick Start Guide 🚀

## ✅ All Issues Fixed

### 1. CSS Errors ✓
- Fixed linear-gradient syntax (changed from `135deg` to `to bottom right`)
- All CSS gradients now use proper JavaFX syntax
- No more CSS parsing warnings

### 2. FXML Errors ✓
- Fixed `HBox.hgrow="ALWAYS"` issues (removed invalid static properties)
- Replaced with `Region HBox.hgrow="ALWAYS"` where needed
- Fixed all `VBox.vgrow="ALWAYS"` issues
- Both dashboards now load properly

### 3. Blue Color Theme ✓
- Updated all button gradients to match blue theme
- Changed from purple-heavy to blue-dominant colors
- Primary blue: #667eea
- All hover effects use blue tones

### 4. Credix Branding ✓
- Added "Credix" name throughout the app
- Created blue gradient circle logo with $ symbol
- Updated all window titles with "Credix" prefix
- Logo appears in Login, Signup, and both Dashboards

### 5. Input Validation (Contrôle de Saisie) ✓
**Login:**
- Email format validation
- Password length check (min 6 characters)
- Real-time error clearing
- Field focus on error

**Signup:**
- First & Last name validation (letters only)
- Comprehensive email validation
- Phone number validation (8-15 digits, + supported)
- Date of birth validation (18+ years, not future)
- Password strength (min 6 chars, must contain letters)
- Password confirmation matching
- Terms checkbox requirement
- Field-specific error messages with focus

### 6. Dashboards Fixed ✓
- Client Dashboard loads without errors
- Admin Dashboard loads without errors
- All tables properly configured
- Responsive layouts

## 🎯 How to Run

### Step 1: Database Setup
```sql
CREATE DATABASE finance1;
USE finance1;
SOURCE database/finance1.sql;
```

### Step 2: Configure Database
Edit `src/main/java/tn/esprit/config/DBConnection.java`:
```java
public final String PWD = "your_mysql_password";
```

### Step 3: Run Application
```bash
mvn clean javafx:run
```

Or double-click: `run.bat`

## 📝 Test Accounts

Create test accounts using the Signup page, or manually insert:

```sql
-- Admin Account
INSERT INTO users (nom, prenom, email, mot_de_passe, telephone, date_naissance, role, statut_compte)
VALUES ('Admin', 'System', 'admin@credix.com', 'admin123', '+1234567890', '1990-01-01', 'admin', 'actif');

-- Client Account
INSERT INTO users (nom, prenom, email, mot_de_passe, telephone, date_naissance, role, statut_compte)
VALUES ('Doe', 'John', 'client@credix.com', 'client123', '+1234567891', '1995-05-15', 'client', 'actif');
```

## 🎨 Design Features

- **Blue Gradient Theme**: Modern blue tones matching the dashboard template
- **Credix Logo**: Blue circle with $ symbol
- **Smooth Animations**: Hover effects and transitions
- **Responsive Layout**: Works on different screen sizes
- **Professional UI**: Card-based design with shadows

## 📋 Validation Rules Summary

| Field | Rules |
|-------|-------|
| Email | Must be valid format (user@domain.com) |
| Password | Min 6 characters, must contain letters |
| Names | Letters only (including accented) |
| Phone | 8-15 digits, + allowed for international |
| Age | Must be 18+ years old |
| Date | Cannot be in future |

## 🔧 Troubleshooting

**If CSS errors persist:**
```bash
mvn clean install
```

**If dashboards don't load:**
- Check database connection
- Verify user exists in database
- Check console for detailed errors

**If login fails:**
- Ensure database `finance1` exists
- Verify user credentials in database
- Check account status is 'actif'

## 📁 Key Files Modified

- ✅ `style.css` - Fixed gradients, added blue theme
- ✅ `Login.fxml` - Added Credix logo, fixed imports
- ✅ `Signup.fxml` - Added Credix logo, scrollable form
- ✅ `ClientDashboard.fxml` - Fixed layout issues
- ✅ `AdminDashboard.fxml` - Fixed layout issues
- ✅ `LoginController.java` - Enhanced validation
- ✅ `SignupController.java` - Comprehensive validation
- ✅ All window titles updated with "Credix"

## 🎉 Ready to Use!

The application is now fully functional with:
- ✓ No CSS errors
- ✓ No FXML errors  
- ✓ Blue theme matching template
- ✓ Credix branding
- ✓ Comprehensive validation
- ✓ Working dashboards

Run `mvn javafx:run` and enjoy! 🚀
