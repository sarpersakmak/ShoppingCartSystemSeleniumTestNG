# Shopping Cart System Selenium TestNG Project

## 📋 Project Overview

This project contains automated web testing solutions using Selenium WebDriver and TestNG, including:
- **Task 1**: Price comparison across multiple e-commerce sites
- **Task 2**: Login functionality testing on multiple browsers
- **Task 3**: Factory pattern demonstration with TestNG
- **Main Project**: Automated web information retrieval with ChatGPT summarization

## 🏗️ Project Structure

```
SeleniumTestNGHomework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/selenium/homework/
│   │   │       ├── task1/
│   │   │       │   └── PriceComparatorTest.java
│   │   │       ├── task2/
│   │   │       │   └── LoginTest.java
│   │   │       ├── task3/
│   │   │       │   ├── FactoryTest.java
│   │   │       │   └── FactoryTestRunner.java
│   │   │       └── mainproject/
│   │   │           ├── WebInfoRetriever.java
│   │   │           └── PDFReportGenerator.java
│   │   └── resources/
│   │       ├── login.html
│   │       └── success.html
│   └── test/
│       └── java/
├── pom.xml
├── testng.xml
└── README.md
```

## 🚀 Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Eclipse IDE (or IntelliJ IDEA)
- Chrome and Firefox browsers installed

### Installation Steps

1. **Clone/Import Project**
   ```bash
   # If you have the project as a folder
   File > Import > Existing Maven Projects
   # Select the project folder
   ```

2. **Update Maven Dependencies**
   ```bash
   # In Eclipse: Right-click project > Maven > Update Project
   # Or via command line:
   mvn clean install
   ```

3. **Create HTML Files**
   - Create folder: `src/main/resources/`
   - Add `login.html` and `success.html` (provided in artifacts)

4. **Configure OpenAI API Key** (for Main Project)
   - Open `WebInfoRetriever.java`
   - Replace `YOUR_OPENAI_API_KEY` with actual API key
   - (Optional: Project will use fallback summarization if key not provided)

## 📝 Running the Tests

### Option 1: Run via TestNG XML
```bash
# Right-click on testng.xml > Run As > TestNG Suite
```

### Option 2: Run Individual Tasks
```bash
# Task 1: Price Comparator
Right-click PriceComparatorTest.java > Run As > TestNG Test

# Task 2: Login Test
Right-click LoginTest.java > Run As > TestNG Test

# Task 3: Factory Test
Right-click FactoryTest.java > Run As > TestNG Test
```

### Option 3: Run Main Project
```bash
# Right-click WebInfoRetriever.java > Run As > Java Application
# Enter keyword when prompted
```

### Option 4: Maven Command Line
```bash
# Run all TestNG tests
mvn test

# Run specific test
mvn test -Dtest=PriceComparatorTest
```

## 📊 Task Details

### Task 1: Price Comparator
**Objective**: Compare iPhone 15 prices across Amazon, BestBuy, and Apple

**Features**:
- Automated price extraction
- Statistical analysis (min, max, average)
- Formatted console report
- TestNG assertions

**Running**:
```java
// The test will automatically:
// 1. Visit each website
// 2. Search for "iPhone 15"
// 3. Extract prices
// 4. Generate comparison report
```

**Expected Output**:
```
============================================================
           IPHONE 15 PRICE COMPARISON REPORT
============================================================

Prices from all sites:
  Amazon:         $799.00
  BestBuy:        $829.99
  Apple:          $799.00

------------------------------------------------------------
✓ CHEAPEST:      $799.00 from Amazon
✗ MOST EXPENSIVE: $829.99 from BestBuy
⊕ AVERAGE PRICE:  $809.33
------------------------------------------------------------
```

### Task 2: Login Test
**Objective**: Test login functionality with correct/incorrect credentials

**Test Scenarios**:
1. ✅ Successful login (admin/password)
2. ❌ Wrong username
3. ❌ Wrong password
4. ❌ Both credentials wrong
5. ❌ Empty fields

**Browsers Tested**:
- Chrome
- Firefox

**Running**:
```bash
# Ensure HTML files are in src/main/resources/
# Test will automatically load them using file:/// protocol
```

### Task 3: Factory Pattern
**Objective**: Demonstrate @Factory annotation with multiple test instances

**Factory Creates**:
- Chrome + Google
- Firefox + Google
- Chrome + Wikipedia
- Firefox + GitHub

**Key Concept**: Each instance runs all @Test methods with different parameters

### Main Project: Web Information Retrieval
**Objective**: Automated research with ChatGPT summarization

**Workflow**:
1. 🔍 Get keyword from user
2. 🌐 Search Google with Selenium
3. 📄 Extract top 5 URLs
4. 📥 Fetch page content with OkHttp
5. 🧹 Parse HTML with Jsoup
6. 🤖 Summarize with ChatGPT
7. 📊 Generate comprehensive report
8. 📄 Export to PDF (optional)

**Running**:
```bash
Enter search keyword: Artificial Intelligence
# System will automatically process and generate report
```

## 🔧 Configuration

### Browser Settings
Both Chrome and Firefox are configured automatically via WebDriverManager. No manual driver setup required!

### Timeouts
```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

### API Keys
For Main Project, set OpenAI API key in `WebInfoRetriever.java`:
```java
private static final String OPENAI_API_KEY = "sk-...your-key...";
```

## 🐛 Troubleshooting

### Common Issues

1. **WebDriver Not Found**
   ```
   Solution: WebDriverManager handles this automatically
   If issue persists: mvn clean install
   ```

2. **HTML Files Not Found**
   ```
   Error: File not found: login.html
   Solution: Ensure files are in src/main/resources/
   Check file paths in LoginTest.java
   ```

3. **OpenAI API Error**
   ```
   Warning: Could not initialize OpenAI service
   Solution: System uses fallback summarization automatically
   No action needed for testing purposes
   ```

4. **Firefox Not Found**
   ```
   Error: Cannot find firefox binary
   Solution: Install Firefox or skip Firefox tests
   Modify testng.xml to run Chrome tests only
   ```

5. **Connection Timeout**
   ```
   Error: Timeout waiting for page load
   Solution: Check internet connection
   Increase timeout in driver configuration
   ```

## 📈 Test Results

TestNG generates reports in:
```
test-output/
├── index.html          # Main report
├── emailable-report.html
└── testng-results.xml
```

Open `test-output/index.html` in browser to view detailed results.

## 📦 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Selenium | 4.15.0 | Web automation |
| TestNG | 7.8.0 | Testing framework |
| WebDriverManager | 5.6.2 | Automatic driver management |
| Jsoup | 1.17.1 | HTML parsing |
| OkHttp | 4.12.0 | HTTP requests |
| OpenAI Java | 0.18.2 | ChatGPT integration |
| iText | 5.5.13.3 | PDF generation |

## 🎓 Key Concepts Demonstrated

### 1. Selenium WebDriver
- Multi-browser testing
- Element locators (id, name, cssSelector, xpath)
- Page navigation
- Dynamic waits

### 2. TestNG Features
- `@Test` annotations
- `@BeforeClass` / `@AfterClass`
- `@Parameters` for parameterization
- `@Factory` for dynamic test creation
- Test dependencies
- Assertions

### 3. Page Object Model
- Separation of concerns
- Reusable components
- Maintainable code structure

### 4. Integration Testing
- API integration (OpenAI)
- File I/O
- HTML parsing
- PDF generation

## 💡 Tips for Presentation

1. **Demo Preparation**
   - Run all tests beforehand
   - Prepare screenshots
   - Have backup results ready

2. **Slide Content**
   - Show code snippets
   - Display console output
   - Include test reports
   - Demo live execution

3. **Common Questions**
   - "Why WebDriverManager?" - Automatic driver management
   - "Why TestNG over JUnit?" - Better reporting, parameterization
   - "How to handle dynamic content?" - Explicit waits, robust locators

## 📞 Support

For issues during presentation:
1. Check `test-output/` for error logs
2. Verify all dependencies in `pom.xml`
3. Ensure browsers are up to date
4. Check HTML file paths

## 🎯 Grading Criteria Checklist

- ✅ Task 1: Price comparison working
- ✅ Task 2: Login test on 2 browsers
- ✅ Task 3: Factory pattern demonstrated
- ✅ Main Project: Full workflow implemented
- ✅ TestNG annotations used correctly
- ✅ Code is clean and commented
- ✅ Project runs in Eclipse
- ✅ Ready for presentation

## 📄 License

**Author**: Sarper Sakmak  
**Course**: Software Validation and Verification  
**Date**: 12/13/2025  
**Version**: 1.0
