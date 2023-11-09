package Dinner;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

public class Filosofo extends Thread {
    private int id;
    private Lock garfoEsquerda, garfoDireita;
    private Random random;
    private static int[] contadorComida = new int[5];
    private static long[] tempoDeAlimentacao = new long[5];

    public Filosofo(int id, Lock garfoEsquerda, Lock garfoDireita) {
        this.id = id;
        this.garfoEsquerda = garfoEsquerda;
        this.garfoDireita = garfoDireita;
        this.random = new Random();
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Filosofo " + id + " esta pensando.");
                Thread.sleep(random.nextInt(1000));
                
                  long tempoInicioRefeicao = System.currentTimeMillis();
                
                if (garfoEsquerda.tryLock() && garfoDireita.tryLock()) {
                    System.out.println("Filosofo " + id + " pegou ambos os garfos e está comendo.");
                    Thread.sleep(random.nextInt(1000));
                    contadorComida[id]++;
                    
                    garfoEsquerda.unlock();
                    garfoDireita.unlock();
                } else {
                    System.out.println("Filosofo " + id + " nao conseguiu pegar ambos os garfos.");
                }
                
                long tempoFimRefeicao = System.currentTimeMillis();
                long tempoAlimentacao = tempoFimRefeicao - tempoInicioRefeicao;
                tempoDeAlimentacao[id] += tempoAlimentacao;
            }
        } catch (InterruptedException e) {}
    }

    public static void main(String[] args) {
        int n = 5;
        Filosofo[] filosofos = new Filosofo[n];
        Lock[] garfos = new ReentrantLock[n];

        for (int i = 0; i < n; i++) {
            garfos[i] = new ReentrantLock();
        }

        for (int i = 0; i < n; i++) {
            filosofos[i] = new Filosofo(i, garfos[i], garfos[(i + 1) % n]);
            filosofos[i].start();
        }

        try {
            Thread.sleep(5000); // Tempo limite do jantar
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        for (Filosofo filosofo : filosofos) {
            filosofo.interrupt();
        }

        System.out.println("Tempo de alimentacao de cada filosofo:");
        for (int i = 0; i < n; i++) {
            System.out.println("Filosofo " + i + ": " + tempoDeAlimentacao[i] + " ms");
        }

        for (int i = 0; i < n; i++) {
            System.out.println("Filosofo " + i + " comeu " + contadorComida[i] + " pedacos de comida.");
        }

        System.out.println("Tempo de alimentacao de cada filosofo e quantidade de vezes que comeu:");                                                  
        for (int i = 0; i < n; i++) {                                                                                                                  
            System.out.println("Filosofo " + i + ": Tempo de alimentacao - " + tempoDeAlimentacao[i] + " ms, Comeu - " + contadorComida[i] + " vezes");
    }
}
}