package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2BrouchoudLechaireTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldCountStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("olivier");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void thePortShouldBe2613() {
    assertEquals(2613, RouletteV2Protocol.DEFAULT_PORT);
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldClearStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());

    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "BrouchoudLechaire")
  public void theServerShouldFetchStudentsList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    String student1 = "sacha";
    String student2 = "olivier";
    String student3 = "fabienne";
    client.loadStudent(student1);
    client.loadStudent(student2);
    client.loadStudent(student3);

    assertEquals(student1, client.listStudents().get(0).getFullname());
    assertEquals(student2, client.listStudents().get(1).getFullname());
    assertEquals(student3, client.listStudents().get(2).getFullname());
  }
}