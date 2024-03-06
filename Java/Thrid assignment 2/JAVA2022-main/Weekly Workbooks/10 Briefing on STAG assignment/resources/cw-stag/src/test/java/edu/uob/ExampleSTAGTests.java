package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
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
        sendCommandToServer("simon:open trapdoor");
        response = sendCommandToServer("simon:look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"), "You didn't open the trapdoor");
        sendCommandToServer("simon:look");
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon:look");
        response = response.toLowerCase();
        assertTrue(response.contains("elf"), "You didn't open the trapdoor");
    }

    @Test
    void testChop()
    {
        String response;
        sendCommandToServer("simon: look");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: look");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: look");
        sendCommandToServer("simon:cut cut chop cut down tree");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertFalse(response.contains("tree"), "your command about cut is false");
    }

    @Test
    void testDrink1()
    {
        String response;
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon:drink potion");
        response = sendCommandToServer("simon:inv");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "your command about drink is false");
    }

    @Test
    void testDrink2()
    {
        String response;
        sendCommandToServer("simon:drink potion");
        response = sendCommandToServer("simon:inv");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "your command about drink is false");
    }

    @Test
    void testDrink3()
    {
        String response;
        sendCommandToServer("simon:get potion");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: fight elf");
        sendCommandToServer("simon: fight elf");
        sendCommandToServer("simon: drink potion");
        sendCommandToServer("simon: fight elf");
        response = sendCommandToServer("simon:look");
        response = response.toLowerCase();
        assertTrue(response.contains("cellar"), "your command about drink is false");
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
        sendCommandToServer("simon: fight elf");
        sendCommandToServer("simon: fight elf");
        sendCommandToServer("simon: fight elf");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"),"your command about fight is false");
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
        sendCommandToServer("simon: pay elf");
        sendCommandToServer("simon: pay elf");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("shovel"),"your command about pay is false");
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
        sendCommandToServer("simon: bridge river");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("clearing"), "your command about bridge is false");
    }

    @Test
    void testDig()
    {
        String response;
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: inv");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: look");
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
        sendCommandToServer("simon: dig ground");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("hole"), "Your dig command is false");
    }

    @Test
    void testBlow()
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
        sendCommandToServer("simon: blow horn");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("cutter"), "The lumberjack doesn't show up");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: blow horn");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("a burly wood cutter"), "The lumberjack doesn't show up");
    }
    @Test
    void testName(){
        String response;
        response = sendCommandToServer("simon,_ code: get coin");
        response = response.toLowerCase();
        assertTrue(response.contains("the user name is invalid!"), "Your user name suppose to be invalid");
    }

    @Test
    void testMultiPlayer(){
        String response;
        sendCommandToServer("simon: look");
        response = sendCommandToServer("Liam: look");
        response = response.toLowerCase();
        assertTrue(response.contains("simon"), "You can't see other players!");
        sendCommandToServer("simon: get potion");
        response = sendCommandToServer("Liam: look");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "The potion should not be there!");
        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("Liam: look");
        response = response.toLowerCase();
        assertFalse(response.contains("simon"), "The potion should not be there!");
    }

    @Test
    void testInput(){
        String response;
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: cut tree from forest");
        response = response.toLowerCase();
        assertTrue(response.contains("invalid"), "You can't see other players!");
    }

    @Test
    void testInput1(){
        String response;
        sendCommandToServer("simon: goto dark forest");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("tree"), "You handle the command in a wrong way!");
    }
    @Test
    void testInput2(){
        String response;
        sendCommandToServer("simon: goto dark forest");
        response = sendCommandToServer("simon: look forest");
        response = response.toLowerCase();
        assertTrue(response.contains("invalid"), "You handle the command in a wrong way!");
    }

    @Test
    void testInput3(){
        String response;
        sendCommandToServer("simon: goto forest forest");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("tree"), "You handle the command in a wrong way!");
    }
    @Test
    void testCut1(){
        String response;
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: pull down tree");
        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertFalse(response.contains("tree"), "You can't see other players!");
    }
    @Test
    void testFightElfHealth() {
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open trapdoor");
        sendCommandToServer("simon: goto cellar");
        String response =  sendCommandToServer("simon: fight angry elf");// reduce the health level to 2
        response = response.toLowerCase();
        assertTrue(response.contains("you attack the elf, but he fights back and you lose some health"), "Failed to return execute fight elf action command narration");

        response =  sendCommandToServer("simon: health");
        response = response.toLowerCase();
        assertTrue(response.contains("2"),"Failed to execute correct health after fighting elf action command");

        sendCommandToServer("simon: drink potion"); // add health level to 3
        response =  sendCommandToServer("simon: health");
        response = response.toLowerCase();
        assertTrue(response.contains("3"),"Failed to execute correct health after drinking potion action command");

        sendCommandToServer("simon: hit angry elf");
        sendCommandToServer("simon: please attack the angry elf");
        response = sendCommandToServer("simon: elf fight");
        response = response.toLowerCase();
        assertTrue(response.contains("you died"), "Failed to return death information");

        response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("a log cabin in the woods"), "Failed to execute death action");

        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertFalse(response.contains("potion"), "Failed to execute drink potion action");
        assertFalse(response.contains("axe"), "Failed to execute drop entities in inventory to death location action");

        response = sendCommandToServer("simon: goto cellar");
        response = response.toLowerCase();
        assertTrue(response.contains("axe"), "Failed to execute drop entities in inventory to death location action");
    }

}