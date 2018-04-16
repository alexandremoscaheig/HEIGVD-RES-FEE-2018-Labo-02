package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2li123Test {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


  @Test
  @TestAuthor(githubId = "l-i-123")
  public void testGetProtocolVersion_theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "l-i-123")
  public void testClearDataStore_theServerShouldClearTheStudentList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    client.clearDataStore();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "l-i-123")
  public void testListStudents_emptyList() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    assertTrue(client.listStudents().isEmpty());
  }

  @Test
  @TestAuthor(githubId = "l-i-123")
  public void testListStudents_notEmptyList() throws IOException {
    String student1 = "student1";
    String student2 = "student2";

    Set<Student> students = new HashSet<>();
    students.add(new Student(student1));
    students.add(new Student(student2));

    IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
    client.loadStudent(student1);
    client.loadStudent(student2);
    assertEquals(students, new HashSet(client.listStudents()));
  }

}
