# Restaurant Ordering System

A Java-based ordering system for restaurant or food service applications with GUI interface, file handling, and comprehensive testing. The system enables customers to browse menus, manage orders, and track their order history through an intuitive graphical interface.

## Features

### GUI Interface (AWT/Swing)
- **Menu Browsing Screen**
  - View all items with details (name, price, availability)
  - Interactive navigation with search and filter options
  - Real-time availability updates
  - Category-based filtering
  - Price-based sorting

- **Order Management Screen**
  - View pending orders
  - Track order status (preparing, out for delivery)
  - Order details display (order number, items, quantities)
  - Cancel order functionality

### Core Functionalities
- **Menu Management**
  - Browse complete menu
  - Search items by keywords
  - Filter by category
  - Sort by price
  - Check item availability

- **Cart Management**
  - Add/remove items
  - Update quantities
  - View cart total
  - Checkout process

- **Order Processing**
  - Place orders
  - Track order status
  - Cancel orders
  - View order history

- **Review System**
  - Add reviews for purchased items
  - View item reviews

### File Handling
- **Order History Storage**
  - Individual user order history tracking
  - Persistent storage of order details
  - Order metadata (items, quantities, prices)

- **Cart Data Management**
  - Temporary cart storage during sessions
  - Real-time cart updates
  - Session persistence

## Project Structure
```
src/
├── gui/
│   ├── MenuBrowserScreen.java    # GUI for menu browsing
│   └── OrderTrackingScreen.java  # GUI for order management
├── CLI/
│   ├── Admin.java                 # admin operations
│   └── Customer.java             # Customer unit tests
|
└── test/
    ├── CartTestRunner.java       # Cart test suite, to be run independently
    └── order_avail_tests.java # Order validation suite, to be run independently
```

## Testing Framework

### Cart Testing (CartTest.java)
- **Cart Operations Tests**
  - Add item validation
  - Total price calculation
  - Quantity modification
  - Negative quantity prevention
  - Cart update verification

### Order Availability Testing (order_avail_tests.java)
- **Stock Management Tests**
  - Out-of-stock order prevention
  - Error message validation
  - Order processing verification
  - Stock level checking

### Test Runners
- **CartTestRunner**
  - Executes cart operation test suite
  - Validates cart functionality

- **order_avail_tests**
  - Runs stock management tests
  - Verifies order validation logic

## Setup & Installation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- JUnit 4.x or higher for testing

### Compilation
```bash
javac -d bin src/**/*.java
```

### Running Tests
```bash
java org.junit.runner.JUnitCore CartTestRunner
java org.junit.runner.JUnitCore order_avail_tests
```

### Launching the Application
```bash
java -cp bin main.Main
```

## Usage Guide

### CLI
1. Admin Operations
2. Customer Operations

### GUI
1. View Pending Orders
2. View current menu items

## File Storage

### Order History
- Location: `order_history.txt`
- Format: JSON structure with order details
- Updates: Real-time after order completion

### Cart Storage
- Location: `pending_orders.txt`
- Format: Serialized cart data
- Updates: Real-time during session
