## General

E-Commerce Demo App. Add a product and view it

<a id="raw-url" href="files/apk/E-Commerce Demo.apk?raw=true"><img src="https://raw.githubusercontent.com/nasim0x1/nasim0x1/main/image/download.svg"  width="180" height=auto>
</a>

## App Info

- **Authentication** Added Email and Password authentication
- **Account Type** Two types of account. One is individual and another is admin account
- **Account Type Info** Individual users can only view products but admins can view and add products

## Specs / Open-source libraries:

- Minimum **SDK 21**, _but AppCompat is used all the way ;-)_
- [**Java**](https://www.java.com/) Used Java as the primary language of the app.
- [**Firebase**](https://firebase.google.com) Used Firebase as the backend for this app
- [**Picasso**](https://square.github.io/picasso/) Used Picasso library for showing the images coming from backend

## Screenshots

|                       Add new Product                        |                        Home Page                        |                        Details Page                         |
| :------------------------------------------------------------------: | :-----------------------------------------------------------------: | :------------------------------------------------------------------: |
| <img src="files/screenshots/add_product.gif" width=272 height=auto>  | <img src="files/screenshots/home.gif" width=272 height=auto> | <img src="files/screenshots/product_details.gif" width=272 height=auto>  |

## N.B: Apply Dependencies

- Add Picasso Dependency
```
implementation 'com.squareup.picasso:picasso:2.71828'
```

- Add Firebase Dependencies
```
implementation 'com.google.firebase:firebase-database:20.0.3'
implementation 'com.google.firebase:firebase-auth:21.0.1'
implementation 'com.google.firebase:firebase-storage:20.0.0'
```

- Add this to build script dependency
```
classpath 'com.google.gms:google-services:4.3.10'
```

