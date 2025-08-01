package com.seletest.tests;

import com.seletest.utils.DriverManager;
import com.seletest.utils.TestDataHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {

  protected static final String BASE_URL = "http://localhost:3000";
  protected static final String BROWSER = System.getProperty("browser", "chrome");

  @BeforeEach
  public void setUp() {
    try {
      TestDataHelper.clearTestData();
    } catch (Exception e) {
      System.out.println("INFO: API-based cleanup failed, will use UI-based cleanup in tests");
    }

    DriverManager.setDriver(BROWSER);
  }

  @AfterEach
  public void tearDown() {
    DriverManager.quitDriver();
  }
}

