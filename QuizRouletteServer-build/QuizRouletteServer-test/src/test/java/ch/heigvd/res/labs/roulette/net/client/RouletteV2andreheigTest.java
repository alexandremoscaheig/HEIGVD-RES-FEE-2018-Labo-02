package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;
import jdk.nashorn.internal.parser.JSONParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2andreheigTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "andreheig")
  public void version2ShouldCountStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("pikachu");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("goku");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("flubber");
    assertEquals(3, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "andreheig")
  public void version2ShouldReset() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("pikachu");
    client.loadStudent("goku");
    client.loadStudent("flubber");
    assertEquals(3, client.getNumberOfStudents());
    client.clearDataStore();
    assertEquals(0,client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "andreheig")
  public void version2ShouldList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("pikachu");
    client.loadStudent("goku");
    client.loadStudent("flubber");
    
    List<Student> listExpected  = client.listStudents();
    
    assertTrue(listExpected.get(0).getFullname().equals("pikachu"));
    assertTrue(listExpected.get(1).getFullname().equals("goku"));
    assertTrue(listExpected.get(2).getFullname().equals("flubber"));
    
  }

  @Test
  @TestAuthor(githubId = "andreheig")
  public void version2ShouldNotEmptyWithData() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("bozo");
    assertFalse(client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = "andreheig")
  public void version2ShouldStartEmpty() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertTrue("true",client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = "andreheig")
  public void version2ShouldGetGoodVersion() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertEquals(RouletteV2Protocol.VERSION,client.getProtocolVersion());
  }
}
