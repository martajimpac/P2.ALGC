package com.company;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.*;

/**
 * Calcular la cantidad de lanzamientos necesarios para calcular
 * cuántos lanzamientos serían necesarios para que la probabilidad
 * de haber obtenido el máximo global sea p = 0.9999.
 * @author Marta Jimenez
 */
public class Tarea1{
    static double maxRecorrido=0; //maximo del recorrido que hace el algoritmo
    static int nFracasos = 0;

    /**
     * @param n Dimensión de la matriz
     * @return Matriz con la función representada
     *
     * Función que crea una matriz de puntos equidistantes(i,j) en un rango de valores dado (sera distinto dependiendo
     * de la función elegida) y luego recorre la matriz calculando para cada punto el valor de la función que hemos elegido.
     */
    public static double[][] creaMatriz(int n) {
        double x0, x1, i, j;

        x0 = -1.5;
        x1 = 2.5;

        Vector[][] matriz = new Vector[n][n];
        double[][] funcion = new double[n][n];
        for (int f = 0; f < n; f++) {
            for (int c = 0; c < n; c++) { //Crear la matriz de puntos equidistantes
                i = x0 + (double) f * (x1 - x0) / (n - 1);
                j = x0 + (double) c * (x1 - x0) / (n - 1);

                matriz[f][c] = new Vector(i, j);

                funcion[f][c] = cos((i*i+j*j)*12)/(2*((i*i+j*j)*3.14+1));
            }
        }
        return (funcion);
    }

    /**
     * @param n Dimension de la matriz.
     * @param recorrido Matriz que guarda los puntos por los que pasa el recorrido para hallar el máximo
     *                  valores: 1 para punto inicial, 2 para puntos intermedios, 3 para punto final.
     * @param funcion Matriz en la que está representada la función.
     * @return recorrido
     *
     * Función que escoge un punto de comienzo aleatorio y mira el valor de los 4 puntos colindantes de la matriz
     * y avanza al que tenga mayor valor. Si el valor en el punto actual es mayor o igual que los colinantes termina.
     */
    public static int[][] algoritmoVoraz(int n,int[][] recorrido, double[][] funcion) {
        int x,y;
        x = (int)Math.round(Math.random() * (n-1));
        y = (int)Math.round(Math.random() * (n-1));
        recorrido[x][y] = 1;
        int xMax = 0;
        int yMax = 0;

        while (true) {
            //ver cual es el elemento mayor de los colindantes
            for (int f = -1; f <= 1; f++) {
                for (int c = -1; c <= 1; c++) {
                    if ((f != 0 && c != 0) || 0 > x + f || x + f >= n || 0 > y + c || y + c >= n || (f == 0 && c == 0))
                        continue;
                    if (funcion[x + f][y + c] > funcion[xMax][yMax]) {
                        xMax = x + f;
                        yMax = y + c;
                    }
                }
            }
            //Si el valor en mi punto es mayor o igual que los colindantes terminar
            if (funcion[x][y] >= funcion[xMax][yMax]) break;

            //Avanzar al que tenga el mayor valor.
            x = xMax;
            y = yMax;
            recorrido[x][y] = 2;

        }
        recorrido[x][y] = 3;
        maxRecorrido = funcion[x][y];
        return (recorrido);
    }

    /**
     * @param funcion Matriz en la que está representada la función.
     * @param recorrido Matriz que nos indica los puntos de la función por los que van los recorridos del algoritmo.
     * @param nombreImg Nombre de la imagen en la que vamos a guardar el mapa de calor.
     *
     * Función que dibuja el HeatMap y los recorridos del algoritmo y la exporta a una imagen PNG.
     * Para hacer esto he usado una libreria, la he añadido como clase en mi proyecto y la he modificado para que
     * pinte los recorridos.
     */
    public static void dibujaMapa(double[][] funcion, int[][]recorrido, String nombreImg) throws IOException {
        HeatMap map = new HeatMap(funcion);

        map.setHighValueColour(Color.YELLOW);
        map.setLowValueColour(Color.BLUE);
        map.setShowXAxisValues(false);
        map.setShowYAxisValues(false);
        map.setColourScale(1.4);

        map.saveToFile(new File(nombreImg), recorrido);
    }

    /**
     * Función que ejecuta el main tantas veces como le digamos y comprueba en cada ejecución si hemos obtenido
     * el máximo real con el algoritmo probabilistico.
     * Si no obtenemos el máximo real aumentamos el contador de ejecuciones fracasadas.
     * @param p
     * @param n
     * @throws IOException
     */
    public static void ejecutaMain(int p, int n) throws IOException {

        double[][] funcion = creaMatriz(n);

        //Calcular máximo de la funcion recorriendo la matriz TAREA 1
        double max = 0;
        for (int f = 0; f < n; f++) {
            for (int c = 0; c < n; c++) {
                if (funcion[f][c] > max) {
                    max = funcion[f][c];
                }
            }
        }

        //Calcular máximo de la función con el algoritmo probabilistico (si p=1 algoritmo voraz)
        double maxFuncion=0;
        int[][] recorrido = new int[n][n];
        //Random random = new Random(semilla);
        for (int k = 0; k < p; k++) {
            recorrido = algoritmoVoraz(n,recorrido,funcion);
            if(maxRecorrido>maxFuncion) maxFuncion = maxRecorrido;
        }

        if(max!=maxFuncion) nFracasos++;
        //String nombreFich = "FuncionTarea.1 P" + p + "-N" + n + ".png";
        //dibujaMapa(funcion,recorrido,nombreFich);
    }

    public static void main(String[] args) throws IOException {
        int nEjecuciones = 10000;

        //PARAMETROS DE ENTRADA
        int p; //Numero de veces que realizamos el algoritmo voraz.
        int n = 100; //Dimension de la matriz
        System.out.println("FuncionTarea1. Dimensión: " + n);

       for(p=10;p<290;p=p+10){ //probamos con diferente numero de lanzamientos

            for (int i = 0; i < nEjecuciones; i++) {
                ejecutaMain(p,n);
            }

            int nExitos = nEjecuciones - nFracasos;
            double porcentaje = (double)nExitos/nEjecuciones;
            System.out.println("Nº lanzamientos: " + p);
            System.out.println("Porcentaje: " + porcentaje*100 + "%");
            System.out.println();
            nFracasos = 0;
        }
    }

}
