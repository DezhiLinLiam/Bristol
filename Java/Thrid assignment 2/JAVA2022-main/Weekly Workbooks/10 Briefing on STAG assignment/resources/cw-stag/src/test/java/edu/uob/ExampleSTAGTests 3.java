package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;

class ExampleSTAGTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
      return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
      "Server took too long to respond (probably stuck in an infinite loop)");
  }

  // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
  @Test
  void testLook() {
    String response = sendCommandToServer("simon: look");
    response = response.toLowerCase();
    assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    assertTrue(response.contains("forest"), "Did not see available paths in response to look");
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
  }

  @Test
  void testDrop()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
      sendCommandToServer("simon: drop potion");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Potion isn't on the spot");
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }

  @Test
  void testOpen()
  {
      String response;
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      response = sendCommandToServer("simon:open trapdoor");
      response = response.toLowerCase();
      assertTrue(response.contains("you unlock the door and see steps leading down into a cellar"), "You didn't open the trapdoor");
  }

  @Test
  void testChop()
  {
      String response;
      sendCommandToServer("simon: get axe");
      sendCommandToServer("simon: goto forest");
      response = sendCommandToServer("simon:cut cut chop cut down tree");
      response = response.toLowerCase();
      assertTrue(response.contains("you cut down the tree with the axe"), "You didn't open the trapdoor");
  }

  @Test
  void testDrink()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon:drink potion");
      response = response.toLowerCase();
      assertTrue(response.contains("your health is already full, it cannot be restored."), "You didn't open the trapdoor");
  }

  @Test
  void testFight()
  {
      String response;
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: open trapdoor");
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: fight elf");
      response = response.toLowerCase();
      assertTrue(response.contains("you attack the elf, but he fights back and you lose some health"));
  }

  @Test
  void testPay()
  {
      String response;
      sendCommandToServer("simon: get coin");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: open trapdoor");
      sendCommandToServer("simon: goto cellar");
      response = sendCommandToServer("simon: pay elf");
      response = response.toLowerCase();
      assertTrue(response.contains("you pay the elf your silver coin and he produces a shovel"));
  }

  @Test
  void testBridge()
  {
      String response;
      sendCommandToServer("simon: get axe");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: cut cut chop cut down tree");
      sendCommandToServer("simon: get log");

      sendCommandToServer("simon: goto riverbank");
      response = sendCommandToServer("simon: bridge river");
      response = response.toLowerCase();
      assertTrue(response.contains("you bridge the river with the log and can now reach the other side"), "You didn't open the trapdoor");
  }

  @Test
  void testDig()
  {
      String response;
      sendCommandToServer("simon: get coin");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: open trapdoor");
      sendCommandToServer("simon: goto cellar");
      sendCommandToServer("simon: pay elf");
      sendCommandToServer("simon: look");
      sendCommandToServer("simon: get shovel");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: get axe");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: cut cut chop cut down tree");
      sendCommandToServer("simon: get log");
      sendCommandToServer("simon: goto riverbank");
      sendCommandToServer("simon: bridge river");
      sendCommandToServer("simon: goto clearing");
      response = sendCommandToServer("simon: dig ground");
      response = response.toLowerCase();
      assertTrue(response.contains("you dig into the soft ground and unearth a pot of gold !!!"), "You didn't open the trapdoor");
  }

  @Test
  void testblow()
  {
      String response;
      sendCommandToServer("simon: get coin");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: get key");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: open trapdoor");
      sendCommandToServer("simon: goto cellar");
      sendCommandToServer("simon: pay elf");
      sendCommandToServer("simon: look");
      sendCommandToServer("simon: get shovel");
      sendCommandToServer("simon: goto cabin");
      sendCommandToServer("simon: get axe");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: cut cut chop cut down tree");
      sendCommandToServer("simon: get log");
      sendCommandToServer("simon: goto riverbank");
      sendCommandToServer("simon: dig ground");
      sendCommandToServer("simon: get horn");
      response = sendCommandToServer("simon: blow horn");
      response = response.toLowerCase();
      assertTrue(response.contains("you blow the horn and as if by magic, a lumberjack appears !"), "You didn't open the trapdoor");
      sendCommandToServer("simon: goto forest");
      sendCommandToServer("simon: blow horn");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("a burly wood cutter"));


  }
}
