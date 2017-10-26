import java.util.Arrays;
import java.util.Random;

public class Knapsack {
	public static TestCase [] TestCases;
	public static Chromosome randomPopulation[];
	public static int popSize = 1000; // can be changed
	public static int maxNumberOfGenerations = 2;// can be changed
	public static Chromosome selectedChromosomes[];
	public static Chromosome offspringChromosomes[];
	
	public static void main(String [] args){
		int testCasesNumber = FileUtility.readTestCasesFile();
		int selectionSize = 0;
		
		if(popSize % 2 == 0){
			selectionSize = popSize;
		}
		else{
			selectionSize = popSize-1;
		}
		
		for(int i=0;i<testCasesNumber;i++){
			int itemsNumber = TestCases[i].Items.length; 
			randomPopulation = new Chromosome[popSize];
			
			Utils.initPopulation(itemsNumber);
			for(int chromosomeIndex=0;chromosomeIndex<popSize;chromosomeIndex++){
				calcFitness(randomPopulation,itemsNumber,TestCases[i]);
			}
			selectedChromosomes = new Chromosome[selectionSize];
					
			for(int c=0;c<maxNumberOfGenerations;c++){
							
				selectChoromosomes(selectionSize);
				offspringChromosomes = Arrays.copyOf(selectedChromosomes, selectionSize);
				
				crossOver(itemsNumber);
				mutation();
				
				for(int itemIndex=0;itemIndex<itemsNumber;itemIndex++){
					calcFitness(offspringChromosomes,itemsNumber,TestCases[i]);
				}
				
				//Replacement
				if(popSize % 2 != 0){
					popSize--;
					randomPopulation = new Chromosome[popSize];
				}
				randomPopulation=Arrays.copyOf(offspringChromosomes, popSize);
				
			}
			
			int max = 0;
			for(int tmp=0;tmp<popSize;tmp++){
				max=Math.max(max,randomPopulation[tmp].fitness);
			}
			System.out.println("Case "+(i+1)+": "+max);
		}
	}
	
	// each item is represented as one bit
	private static void calcFitness(Chromosome population[],int bitsNumber,TestCase testCase){
		int chromosomeBenefit;
		int chromosomeWeight;
		
		for(int chromosomeIndex=0;chromosomeIndex<population.length;chromosomeIndex++){
			chromosomeBenefit = 0;
			chromosomeWeight = 0;
			
			for(int bitIndex=0;bitIndex<bitsNumber;bitIndex++){
				if(population[chromosomeIndex].bits[bitIndex] == 1){
					chromosomeBenefit += testCase.getItemBenefit(bitIndex);
					chromosomeWeight += testCase.getItemWeight(bitIndex);
					
					if(chromosomeWeight > testCase.knapsackSize){
						break;
					}
				}
			}
			
			if(chromosomeWeight > testCase.knapsackSize){
				population[chromosomeIndex].fitness = 0;
			}
			else{
				population[chromosomeIndex].fitness = chromosomeBenefit;
			}
		}
	}
	
	private static void selectChoromosomes(int selectionSize){
		Utils.sortRandomPopulation();
		
		int fitnessSummation = 0;
		int randomNumber;
		
		for(int chromosomeIndex=0;chromosomeIndex<popSize;chromosomeIndex++){
			fitnessSummation += randomPopulation[chromosomeIndex].fitness;
			randomPopulation[chromosomeIndex].fitnessRange = fitnessSummation;
		}
		
		for(int selectedIndex=0;selectedIndex<selectionSize;selectedIndex++){
			randomNumber = (int)(Math.random()*fitnessSummation);
			
			for(int chromosomeIndex=0;chromosomeIndex<popSize;chromosomeIndex++){
				if(randomNumber <= randomPopulation[chromosomeIndex].fitnessRange){
					selectedChromosomes[selectedIndex] = randomPopulation[chromosomeIndex];
					break;
				}
			}
		}
		
	}
	
	private static void crossOver(int itemsNumber) {
		double Pc = 0.6;
		
		for(int i=0;i<selectedChromosomes.length-1;i+=2) {
			Random r1 = new Random();
			int point = r1.nextInt(selectedChromosomes[i].bits.length);
			float r2=r1.nextFloat();
			if(r2<=Pc)
			{
				Chromosome c = new Chromosome(itemsNumber);
				for(int j=0;j<itemsNumber;++j){
					c.bits[j]=offspringChromosomes[i].bits[j];
				}
				offspringChromosomes[i].crossOver(point, offspringChromosomes[i+1]);
				offspringChromosomes[i+1].crossOver(point, c);
			}
			
		}
	}
	private static void mutation()
	{
		double Pm=0.001;
		for(int i=0;i<offspringChromosomes.length;i++)
		{
			for(int j=0;j<offspringChromosomes[i].bits.length;j++)
			{
				Random r = new Random();
				
				if(r.nextFloat()<=Pm)
				{
					if(offspringChromosomes[i].bits[j]==1)
					{
						offspringChromosomes[i].bits[j]=0;
					}
					else if(offspringChromosomes[i].bits[j]==0)
					{
						offspringChromosomes[i].bits[j]=1;
					}
				}
			}
		}
	}
}
