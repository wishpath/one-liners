package org.sa.service;

import java.util.Map;

public class InstructionTextForAi {
  public static String getInstructionToEvaluateUserInput(Map.Entry<String, String> concept, AdditionalInstructionsToEvaluate instruction) {
    String questionB =
        "Is this a good key and definition: key: \"" + concept.getKey() + "\", and definition: \"here_will_be_userInputDefinitionAttempt\". " +
            "\n A. Step 1 - Evaluate the answer by asking: 'Does this capture the essence?' (aim to be positive)." +
            "\n B. If some details are missing but it captures the essence, rate 10/10." +
            "\n C. If the definition matches this one, rate 10/10: " + concept.getValue() + ", but other definitions might get a perfect rating as well." +
            "\n D. If the essence is ALMOST there, rate 9/10." +
            "\n E. If the essence is somewhat touched, rate 8/10." +
            "\n F. Think of an answer in up to 10 words - if you can't come up with a better one, rate 10/10." +
            "\n G. If the answer is completely off, rate 0/10." +
            "\n H. If the answer is somewhat acceptable, rate 7/10." +

            //acronyms
            "\n I. If the key is an acronym, definitions should include the exact matching word for each letter in the definition (e.g., 'Intelligence Quotient' for IQ); case does not matter." +
            "\n J. If the key is an acronym, each core expanded word must be spelled exactly (−1 point per misspelling); Ignore if letters are lowercase or uppercase\n" +
            "\n K. If the key is an acronym, each core expanded word — even if misspelled — must clearly match the key’s intended word; wrong words aren’t accepted (e.g., for “SSL”: “securing” OK (only gramatical form is different), “sekure” −1 point (misspell), “service” rejected (totally wrong word)).\n" +

            "\n Step 2 - If the evaluation is less than 7/10, provide the correct answer (if 7/10 to 10/10, skip this step)." +
            "\n Your entire answer should be up to 300 characters." +

            //instructions for individual concept
            (instruction.key_instructions.containsKey(concept.getKey()) ?
                "\nAdditional instructions: \n" + instruction.getIndividualInstructions(concept.getKey()) :
                "")
        ;
    return questionB;
  }
}
