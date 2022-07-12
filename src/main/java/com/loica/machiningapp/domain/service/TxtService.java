package com.loica.machiningapp.domain.service;

import com.loica.machiningapp.domain.model.Program;
import com.loica.machiningapp.domain.model.Step;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import org.springframework.stereotype.Service;

@Service
public class TxtService {

  public TxtService() {}

  public void createFile(Program program) {
    try {
      String fileName = "./programmes/"+program.getProgramNumber() + " - " + program.getName() + ".txt";
      File myObj = new File(fileName);

      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
      } else {
        System.out.println("File already exists.");
      }
      completeFile(program, fileName);
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  private void completeFile(Program program, String fileName) {
    StringBuilder sb = new StringBuilder();
    if (program.getSteps() != null) {
      program.getSteps().sort(Comparator.comparingInt(Step::getRank));
      program.getSteps().forEach(step -> sb.append(step.getContent()).append("\n\n"));
    }

    try {
      FileWriter myWriter = new FileWriter(fileName);
      myWriter.write(sb.toString());
      myWriter.close();
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
}
