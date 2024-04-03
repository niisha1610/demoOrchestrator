//package com.sap.cic.pdp;
//
//import io.cucumber.java.en.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//class IsItFriday {
//   static String isItFriday(String today) {
//       return "Friday".equals(today) ? "TGIF" : "Nope";
//   }
//}
//
//public class StepDefinition {
//   private String today;
//   private String actualAnswer;
//
//   @Given("is today Sunday")
//   public void today_is_sunday() {
//       // Write code here that turns the phrase above into concrete actions
//       today = "Sunday";
//   }
//   @Given("today is Friday")
//   public void today_is_Friday() {
//       today = "Friday";
//   }
//   @When("I ask whether it's Friday yet")
//   public void i_ask_whether_it_s_friday_yet() {
//       // Write code here that turns the phrase above into concrete actions
//       actualAnswer = IsItFriday.isItFriday(today);
//   }
//   @Then("I should be told {string}")
//   public void i_should_be_told(String expectedAnswer) {
//       assertEquals(expectedAnswer, actualAnswer);
//   }
//
//}