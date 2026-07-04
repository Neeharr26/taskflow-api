package com.neehar.taskflow;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SmokeTest {
	@Test		
	 void onePlusOneIsTwo() {
	        int result = 1 + 1;                  // pretend this is "your code doing something"
	        assertThat(result).isEqualTo(2);     // the check: I expect result to be 2
	    }
}
