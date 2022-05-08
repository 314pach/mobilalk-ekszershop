# mobilalk-ekszershop

Fordítási, futási hiba nálam nem fordult elő ^^

### Firebase autentikáció:
Regisztráció, bejelentkezés email-címmel megvalósítva (valami random is tökéletes)

### Adatmodell definiálása
* A felhasználót és a termékeket is egy-egy osztály reprezentálja

### Legalább 3 különböző activity
A 3 megvan bőven :)

### Beviteli mezők típusa megfelelő
Az emailhez emailt használtam, a jelszóhoz passwordot, a többihez szöveget

### ConstraintLayout és a többi
* A regisztráció és a bejelentkezés ConstraintLayout (álló nézetben), fekvő nézetben LinearLayout
* A profil ConstraintLayout mindkét helyzetben
* A kosár tartalmát mindkét helyzetben LinearLayout tartalmazza
* A main activity mindkét helyzetben RelativeLayout
* A termékek listája ConstraintLayout
* A menüben a piros gomb FrameLayout
* Egy-egy termék CardView-ban van megvalósítva

### Reszponzivitás:
Mindenhez csináltam landscape módot is, ezek között az információáramlás is működik

### 2 különböző animáció
A main activity háttere animált, illetve az kártyák az egyes termékekkel

### Intentek
Minden elérhető

### LifeCycleHook
A legtöbb helyen az onPause-ban elmentettem az adatokat

### Androidos erőforrás
Nem használtam

### Notification
A rendelés leadásának hatására egy notification-t kapunk a végösszeggel

### CRUD műveletek
* Create: új felhasználó eltárolása regisztrációkor
* Read: termékek beolvasása
* Updtae: felhasználónév cseréje
* Delete: felhasználói fiók törlése
(Egyik sem Async)

### 2 komplex Firestore lekérdezés
* A kosárban lévő termékek árának összegzése (CartList.java, sum fgv)
