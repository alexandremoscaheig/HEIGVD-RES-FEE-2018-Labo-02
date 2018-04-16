package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteV2ClientHandler;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 */
public class RouletteV2schranzguTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair2 = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  @Test
  @TestAuthor(githubId ={"wasadigi", "leonardfavre"})
  public void theServerMustDoWhatDidntChangeFromV1() throws IOException, EmptyStoreException {
    assertTrue(roulettePair2.getServer().isRunning());
    assertTrue(roulettePair2.getClient().isConnected());

    int port = roulettePair2.getServer().getPort();
    IRouletteV2Client client1 = new RouletteV2ClientImpl();
    assertFalse(client1.isConnected());
    client1.connect("localhost", port);
    assertTrue(client1.isConnected());

    assertEquals(RouletteV2Protocol.VERSION, roulettePair2.getClient().getProtocolVersion());

    int numberOfStudents = client1.getNumberOfStudents();
    assertEquals(0, numberOfStudents);

    IRouletteV2Client client = (IRouletteV2Client) roulettePair2.getClient();
    assertEquals(0, client.getNumberOfStudents());

    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("olivier");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
    
    client.clearDataStore();

    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "leonardfavre"})
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair2.getClient().getProtocolVersion());
  }



  @Test
  @TestAuthor(githubId = "leonardfavre")
  public void theDefaultPortShouldBeCorrect() throws IOException {
    assertEquals(RouletteV2Protocol.DEFAULT_PORT, roulettePair2.getServer().getPort());
  }

  @Test
  @TestAuthor(githubId = "leonardfavre")
  public void serverLoadCorrectly() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair2.getClient();
    int numberOfStudent = client.getNumberOfStudents();
    client.loadStudent("sacha");
    assertEquals(numberOfStudent+1, client.getNumberOfStudents());
    roulettePair2.getServer().startServer();
    roulettePair2.getClient();
  }

  @Test
  @TestAuthor(githubId = "leonardfavre")
  public void sendingClearToTheServerShouldCleanIt() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair2.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    assertEquals(1, client.getNumberOfStudents());
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    client.loadStudent("bob");
    client.loadStudent("thierry");
    assertEquals(3, client.getNumberOfStudents());
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "leonardfavre")
  public void sendingListToTheServerShouldReturnTheCorrectList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair2.getClient();
    client.clearDataStore();
    client.loadStudent("sacha");
    client.loadStudent("bob");
    
    
    List<Student> students = client.listStudents();
    
    assertTrue(students.get(0).getFullname().equals("sacha"));
    assertTrue(students.get(1).getFullname().equals("bob"));
    
  }
  
}