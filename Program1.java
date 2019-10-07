/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    private int getPreferenceIndex(ArrayList<Integer> preferenceList, int entity) {
        int index = -1;
        for (int i = 0; i < preferenceList.size(); i++) {
            if (preferenceList.get(i) == entity) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    //public static void sort()
    
    /**
     * Compute the preference lists for each internship, given weights and student metrics.
     * Return a ArrayList<ArrayList<Integer>> prefs, where prefs.get(i) is the ordered list of preferred students for
     * internship i, with length studentCount.
     */
    public static ArrayList<ArrayList<Integer>> computeInternshipPreferences(int internshipCount, int studentCount,
                                                                      ArrayList<ArrayList<Integer>>internship_weights,
                                                                      ArrayList<Double> student_GPA,
                                                                      ArrayList<Integer> student_months,
                                                                      ArrayList<Integer> student_projects){

        ArrayList<ArrayList<Integer>> internshipPref = new ArrayList<>(internshipCount);
        
        for(int i = 0; i < internshipCount; i++) {
        	int weightGPA = internship_weights.get(i).get(0);
        	int weightExp = internship_weights.get(i).get(1);
        	int weightProjects = internship_weights.get(i).get(2);
        	ArrayList<Double> ranksWeight = new ArrayList<Double>();
        	ArrayList<Integer> ranks = new ArrayList<Integer>();
        	for(int j = 0; j < studentCount; j++) {
        		double studentGPA = student_GPA.get(j);
        		int studentExp = student_months.get(j);
        		int studentProjects = student_projects.get(j);
        		double studentScore = computeInternshipStudentScore(studentGPA, studentExp, studentProjects, weightGPA, weightExp, weightProjects);
        		//System.out.println(studentScore);
        		//ranks.add(studentScore);
        		if(ranksWeight.size() == 0) {
        			ranksWeight.add(studentScore);
        			ranks.add(j);
        		}
        		else {
        			int size = ranksWeight.size();
	        		for(int k = 0; k < ranksWeight.size(); k++) {
	        			if(studentScore > ranksWeight.get(k)) {
	        				ranksWeight.add(k, studentScore);
	        				ranks.add(k, j);
	        				break;
	        			}
	        		}
	        		if(size == ranksWeight.size()) {
	        			ranksWeight.add(studentScore);
        				ranks.add(j);
	        		}
	        		
        		}
        	}
        	internshipPref.add(ranks);
        	//System.out.println(internshipPref.get(i).toString());
        	
        }

        return internshipPref;
    }
    private static Double computeInternshipStudentScore(double studentGPA, int studentExp, int studentProjects, int
                                                        weightGPA, int weightExp, int weightProjects){
        return studentGPA*weightGPA+studentExp*weightExp+studentProjects*weightProjects;
    }

    /**
     * Determines whether a candidate Matching represents a solution to the Stable Marriage problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    public boolean isStableMatching(Matching marriage) {
        /*TODO implement this function */
    	ArrayList<Integer> student_matching = marriage.getStudentMatching();
    	//System.out.println("Next matching: ");
    	//System.out.println(student_matching);
    	ArrayList<ArrayList<Integer>> intern_preference = marriage.getInternshipPreference();
    	ArrayList<ArrayList<Integer>> intern_weights = marriage.getInternshipWeights();
    	ArrayList<ArrayList<Integer>> student_preference = marriage.getStudentPreference();
    	ArrayList<Integer> internship_slots = marriage.getInternshipSlots();
    	
    	int student_count = marriage.getStudentCount();
    	int internship_count = marriage.getInternshipCount();
    	//System.out.println(intern_preference.toString());
    	//System.out.println(intern_weights);
    	//System.out.println(student_matching);
    	
    	for(int student = 0; student < student_count; student++) {
    		int current_internship = student_matching.get(student);
    		for(int i = 0; i < internship_count; i++) {
    			int compare_internship = student_preference.get(student).get(i);
    			if(current_internship != compare_internship) {
    				int other_student = student_matching.indexOf(compare_internship);
    				if(intern_preference.get(compare_internship).indexOf(student) < intern_preference.get(compare_internship).indexOf(other_student)) {
    					return false;
    				}
    			}
    			else break;
    		}
    	}
    	
    	
    	for(int i = 0; i < student_count; i++) {
    		if(student_matching.get(i) != -1) {
    			int internship = student_matching.get(i);
    			ArrayList<Integer> currentList = intern_preference.get(internship);
	    		for(int j = 0; j < student_count; j++) {
	    			if(j != i & student_matching.get(j) == -1) {
	    				if(currentList.indexOf(j) < currentList.indexOf(i)) {
	    					return false;
	    				}
	    				//System.out.println(currentList);
	    			}
	    		}
    		}
    	}
    	
    	for(int i = 0; i < internship_count; i++) {
    		int slots = internship_slots.get(i);
    		int count = 0;
    		for(int j = 0; j < student_count; j++) {
    			if(student_matching.get(j) == i) {
    				count++;
    			}
    		}
    		if(count > slots) {
    			return false;
    		}
    	}    	
        return true; /* TODO remove this line */
    }
    
    public int getSlotCount(ArrayList<Integer> slots_remaining) {
    	int total = 0;
    	for(int i = 0; i < slots_remaining.size(); i++) {
    		total += slots_remaining.get(i);
    	}
    	return total;
    }
    


    /**
     * Determines a solution to the Stable Marriage problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMarriageGaleShapley_studentoptimal(Matching marriage) {
        /* TODO implement this function */
    	int student_count = marriage.getStudentCount();
    	int internship_count = marriage.getInternshipCount();
    	ArrayList<Integer> student_matching = new ArrayList<Integer>();
    	ArrayList<ArrayList<Integer>> intern_preference = marriage.getInternshipPreference();
    	ArrayList<ArrayList<Integer>> student_preference = marriage.getStudentPreference();
    	ArrayList<Integer> internship_slots = marriage.getInternshipSlots();
    	ArrayList<Integer> slots_remaining = new ArrayList<Integer>(internship_slots);
    	ArrayList<ArrayList<Integer>> internship_matching = new ArrayList<ArrayList<Integer>>();
    	for(int i = 0; i < internship_count; i++) {
    		internship_matching.add(new ArrayList<Integer>());
    	}
     	

    	for(int i = 0; i < student_count; i++) {
    		student_matching.add(-1);
    	}
    	
    	int empty_slots = getSlotCount(slots_remaining);
    	
    	while(empty_slots > 0) {
    		int student = 0;
    		for(student = 0; student < student_count; student++) {
    			if(student_matching.get(student) == -1) {
    				break;
    			}		
    		}
    		for(int i = 0; i < internship_count; i++) {
    			int internship = student_preference.get(student).get(i);
    			int slots = slots_remaining.get(internship);
    			if(slots > 0) {
    				student_matching.set(student, internship);
    				slots_remaining.set(internship, slots-1);
    				empty_slots--;
    				internship_matching.get(internship).add(student);
    				break;
    			}
    			else {
    				
    				boolean breakCheck = false;
    				for(int x = 0; x < internship_slots.get(internship); x++) {
    					if(intern_preference.get(internship).indexOf(internship_matching.get(internship).get(x)) > intern_preference.get(internship).indexOf(student)) {
    						student_matching.set(student, internship);
    						student_matching.set(internship_matching.get(internship).get(x), -1);
    						internship_matching.get(internship).set(x, student);
							breakCheck = true;
							break;
    					}
    				}
    				if(breakCheck)
						break;
					
    				
    			}
    		}
    	}
    	marriage.setStudentMatching(student_matching);
        return marriage; /* TODO remove this line */
    }

    private ArrayList<Matching> getAllStableMarriages(Matching marriage) {
        ArrayList<Matching> marriages = new ArrayList<>();
        int n = marriage.getStudentCount();
        int slots = marriage.totalInternshipSlots();

        Permutation p = new Permutation(n, slots);
        Matching matching;
        while ((matching = p.getNextMatching(marriage)) != null) {
            if (isStableMatching(matching)) {
                marriages.add(matching);
            }
        }

        return marriages;
    }

    @Override
    public Matching stableMarriageBruteForce_studentoptimal(Matching marriage) {
        ArrayList<Matching> allStableMarriages = getAllStableMarriages(marriage);
        Matching studentOptimal = null;
        int n = marriage.getStudentCount();
        int m = marriage.getInternshipCount();
        System.out.println("student" + n + "internship" + m);
        ArrayList<ArrayList<Integer>> student_preference = marriage.getStudentPreference();

        //Construct an inverse list for constant access time
        ArrayList<ArrayList<Integer>> inverse_student_preference = new ArrayList<ArrayList<Integer>>(0) ;
        for (Integer i=0; i<n; i++) {
            ArrayList<Integer> inverse_preference_list = new ArrayList<Integer>(m) ;
            for (Integer j=0; j<m; j++)
                inverse_preference_list.add(-1) ;
            ArrayList<Integer> preference_list = student_preference.get(i) ;

            for (int j=0; j<m; j++) {
                inverse_preference_list.set(preference_list.get(j), j) ;
            }
            inverse_student_preference.add(inverse_preference_list) ;
        }

        // bestStudentMatching stores the rank of the best Internship each student matched to
        // over all stable matchings
        int[] bestStudentMatching = new int[marriage.getStudentCount()];
        Arrays.fill(bestStudentMatching, -1);
        for (Matching mar : allStableMarriages) {
            ArrayList<Integer> student_matching = mar.getStudentMatching();
            for (int i = 0; i < student_matching.size(); i++) {
                if (student_matching.get(i) != -1 && (bestStudentMatching[i] == -1 ||
                        inverse_student_preference.get(i).get(student_matching.get(i)) < bestStudentMatching[i])) {
                    bestStudentMatching[i] = inverse_student_preference.get(i).get(student_matching.get(i));
                    studentOptimal = mar;
                }
            }
        }

        return studentOptimal;
    }
}
