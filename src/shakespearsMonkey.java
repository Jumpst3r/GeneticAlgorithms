import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.Random;

public class shakespearsMonkey extends PApplet {
    static final int SIZE = 1000;
    static final double MUTATION_RATE = 0.01;
    static int gen = 0;
    static String target = "to be or not to be that is the question";
    static DNA[] population = new DNA[SIZE];
    static ArrayList<DNA> matingPool = new ArrayList<>();
    PFont mono;

    public static void main(String[] args) {
        PApplet.main("shakespearsMonkey");
        //initialize population:
        init();

    }

    static void crossOver() {
        Random rnd = new Random();
        int index;
        int index2;
        for (int i = 0; i < population.length; i++) {
            index = rnd.nextInt(matingPool.size());
            index2 = rnd.nextInt(matingPool.size());
            population[i] = matingPool.get(index).crossOver(matingPool.get(index2));
        }
    }

    static void fillMatingPool() {
        matingPool.clear();
        for (DNA genome : population) {
            for (int i = 0; i < (int) genome.fitness; i++) {
                matingPool.add(genome);
            }
        }
    }

    static void init() {
        for (int i = 0; i < population.length; i++) {
            population[i] = new DNA();
            System.out.println(population[i].fitness);
        }
    }

    @Override
    public void setup() {
        mono = createFont("../data/SourceCodePro.ttf", 25);
        background(0);
        stroke(255);
        textFont(mono);
    }

    @Override
    public void settings() {
        size(1600, 300);
    }

    @Override
    public void draw() {
        stroke(255);
        clear();
        line(0,0,12,12);
        fillMatingPool();
        //CrossOver:
        crossOver();
        gen++;
        if (checkPopulation()) {
            frameRate(0);
        }
    }

    boolean checkPopulation() {
        double highest = 0;
        int index = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].fitness > highest) {
                highest = population[i].fitness;
                index = i;
            }
        }
        text(String.format("Best phrase:%40s %20s %.3f", population[index].phenotype, "fitness:", population[index].fitness), 50, 50);
        text(String.format("Target:%45s %20s %4d%%", target, "Mutation rate:", (int) (MUTATION_RATE * 100)), 50, 100);
        text(String.format("Gen: %05d%30s%d", gen, "Initial population size: ",SIZE), 50, 200);
        text(String.format("Elapsed time: %9fs", millis() / 1000.), 50, 250);
        for (DNA member : population) {
            String res = member.getPhenotype();
            if (res.equals(target)) {
                fill(0,255,0);
                text(String.format("%52s %20s %.3f", population[index].phenotype, "fitness:", population[index].fitness), 50, 50);
                return true;
            }
        }
        return false;
    }

    public static class DNA {
        char[] genes = new char[target.length()];
        double fitness;
        String phenotype;

        DNA() {
            for (int i = 0; i < genes.length; i++) {
                Random rnd = new Random();
                genes[i] = (char) (rnd.nextInt(96) + 32);
            }
            this.phenotype = this.getPhenotype();
            fitness();
        }

        String getPhenotype() {
            return new String(genes);
        }

        DNA crossOver(DNA genA) {
            DNA child = new DNA();
            Random rnd = new Random();
            int midpoint = rnd.nextInt(genes.length);
            for (int i = 0; i < genes.length; i++) {
                if (i > midpoint) child.genes[i] = genes[i];
                else child.genes[i] = genA.genes[i];
            }
            return mutate(child);
        }

        void update(DNA node) {
            node.phenotype = node.getPhenotype();
        }

        DNA mutate(DNA child) {
            Random rnd = new Random();
            if ((rnd.nextInt() < (MUTATION_RATE * 100))) {
                child.genes[rnd.nextInt(target.length())] = (char) (rnd.nextInt(96) + 32);
            }
            fitness(child);
            update(child);
            return child;
        }

        void fitness() {
            float score = 0;
            for (int i = 0; i < genes.length; i++) {
                if (genes[i] == target.charAt(i)) {
                    score++;
                }
            }
            this.fitness = Math.pow(score,2);
//            this.fitness = score / target.length();
        }

        void fitness(DNA node) {
            float score = 0;
            for (int i = 0; i < genes.length; i++) {
                if (node.genes[i] == target.charAt(i)) {
                    score++;
                }
            }
            node.fitness = Math.pow(score,2);
//            node.fitness = score / target.length();
        }
    }
}

