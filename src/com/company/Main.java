package com.company;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.*;

/**
 * Programa que inicializa un matriz con valores equidistantes y halla la función dada en esos puntos.
 * Luego halla el máximo de dos formas:
 *   -Recorriendo la función.
 *   -Utilizando algoritmos:
 *        1.Algoritmo voraz: Recibe un punto de inicio aleatorio y va avanzando hacia casillas con mayor valor
 *        hasta alcanzar un máximo (no tiene porque ser global)
 *        2.Algoritmo probabilistico: Repite el algoritmo voraz un numero de veces.
 * @author Marta Jimenez
 */
public class Main{
    static final int N = 100; //tamaño de la matriz
    static final int INICIO = 1;
    static final int INTERMEDIO = 2;
    static final int FINAL = 3;

    /**
     * @param numFuncion Función que vamos a representar
     * @return Matriz con la función representada
     *
     * Función que crea una matriz de puntos equidistantes(i,j) en un rango de valores dado (sera distinto dependiendo
     * de la función elegida) y luego recorre la matriz calculando para cada punto el valor de la función que hemos elegido.
     */
    public static Matriz creaMatriz(int numFuncion) {
        double x0, x1, i, j;
        if (numFuncion == 0) {
            x0 = -1;
            x1 = 1;
        } else {
            x0 = 0;
            x1 = PI;
        }
        double[][] funcion = new double[N][N];
        for (int f = 0; f < N; f++) {
            for (int c = 0; c < N; c++) { //Crear la matriz de puntos equidistantes
                i = x0 + (double) f * (x1 - x0) / (N - 1);
                j = x0 + (double) c * (x1 - x0) / (N - 1);

                switch (numFuncion) { //Representar la función elegida.
                    case (0):
                        funcion[f][c] = j + sin(PI * sqrt(i * i + j * j));
                        break;
                    case (1):
                        funcion[f][c] = sin(i) + cos(j) + sin(i) * cos(j) + sin(i*2);
                        break;
                    case (2):
                        funcion[f][c] = 2 * sin(i) * cos(j/2) + i +  log( abs(j-PI/2));
                        break;
                    case (3):
                        funcion[f][c] = sin(i) * cos(j) + sqrt(i*j);
                        break;
                    case (4):
                        funcion[f][c] = sin( i*7 ) + cos( (j+PI/4)*4 ) + (i+j);
                        break;

                }
            }
        }
        Matriz matriz= new Matriz(funcion);
        return (matriz);
    }

    /**
     * @param matriz
     * recorrido Matriz que guarda los puntos por los que pasa el recorrido para hallar el máximo
     *               valores: 1 para punto inicial, 2 para puntos intermedios, 3 para punto final.
     *  funcion Matriz en la que está representada la función.
     *  max recorrido:
     * @param random Número aleatorio creado con semilla para generar el punto de comienzo.
     * @return recorrido
     *
     * Función que escoge un punto de comienzo aleatorio y mira el valor de los 4 puntos colindantes de la matriz
     * y avanza al que tenga mayor valor. Si el valor en el punto actual es mayor o igual que los colindantes termina.
     */
    public static Matriz algoritmoVoraz(Matriz matriz, Random random) {
        int x = random.nextInt(N - 1);
        int y = random.nextInt(N - 1);

        matriz.recorrido[x][y] = INICIO; //punto inicial
        int xMax = 0;
        int yMax = 0;

        while (true) {
            //ver cuál es el elemento mayor de los colindantes
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if ((abs(i) != abs(j)) && x + i >= 0 && x + i < N && y + j >= 0 && y + j < N ){
                        if (matriz.funcion[x + i][y + j] > matriz.funcion[xMax][yMax]) {
                            xMax = x + i;
                            yMax = y + j;
                        }
                    }
                }
            }

            //Si el valor en mi punto es mayor o igual que los colindantes terminar
            if (matriz.funcion[x][y] >= matriz.funcion[xMax][yMax]) break;

            //Avanzar al que tenga el mayor valor.
            x = xMax;
            y = yMax;
            matriz.recorrido[x][y] = INTERMEDIO; //puntos intermedios
        }
        matriz.recorrido[x][y] = FINAL; //punto final
        matriz.maxRecorrido = matriz.funcion[x][y];
        return (matriz);
    }

    /**
     * @param matriz
     * funcion Matriz en la que está representada la función.
     * Matriz que nos indica los puntos de la función por los que van los recorridos del algoritmo.
     * @param nombreImg Nombre de la imagen en la que vamos a guardar el mapa de calor.
     *
     * Función que dibuja el HeatMap y los recorridos del algoritmo y la exporta a una imagen PNG.
     * Para hacer esto he usado una libreria, la he añadido como clase en mi proyecto y la he modificado para que
     * pinte los recorridos.
     */
    public static void dibujaMapa(Matriz matriz, String nombreImg) throws IOException {
        HeatMap map = new HeatMap(matriz.funcion);

        map.setHighValueColour(Color.YELLOW);
        map.setLowValueColour(Color.BLUE);
        map.setShowXAxisValues(false);
        map.setShowYAxisValues(false);
        map.setColourScale(1.4);

        map.saveToFile(new File(nombreImg), matriz.recorrido);
    }

    public static void main(String[] args) throws IOException {
        //PARAMETROS DE ENTRADA
        int numFuncion = 0; //Funcion que vamos a representar
        int p = 200; //Numero de veces que realizamos el algoritmo voraz.
        int semilla = 55; //Semilla para generar numeros aleatorios
        String nombreFich = "Funcion" + numFuncion + "-P" + p + "-S" + semilla + "-N" + N + ".png";

        Matriz matriz = creaMatriz(numFuncion);

        //Calcular máximo de la funcion recorriendo la matriz NO ES NECESARIO
        double max = 0;
        for (int f = 0; f < N; f++) {
            for (int c = 0; c < N; c++) {
                if (matriz.funcion[f][c] > max) {
                    max = matriz.funcion[f][c];
                }
            }
        }
        System.out.println("Maximo real de la funcion:"+ max);

        //Calcular máximo de la función con el algoritmo probabilistico (si p=1 algoritmo voraz)
        double maxFuncion=0;
        int[][] recorrido = new int[N][N];
        Random random = new Random(semilla);
        for (int k = 0; k < p; k++) {
            matriz = algoritmoVoraz(matriz,random);
            if(matriz.maxRecorrido>maxFuncion) maxFuncion = matriz.maxRecorrido;
        }
        System.out.println("Maximo hallado con algoritmo:"+ maxFuncion);
        dibujaMapa(matriz,nombreFich);
    }
}



