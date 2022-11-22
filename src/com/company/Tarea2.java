package com.company;

import java.util.Random;

import static java.lang.Math.*;

/**
 * Tarea 2. Modificaciones para que el algoritmo probabilistico sea más rapido
 * @author Marta Jimenez
 */


public class Tarea2{

    static double maxRecorrido;
    static int n;
    static double[][] funcion;
    static long tTotalBusq = 0;
    static long tTotalInic = 0;

    /**
     * @param n Dimensión de la matriz
     * @param calcularFuncion si es true calcula el valor de la función
     * @return matriz: Matriz de puntos equidistantes
     *
     * Función que crea una matriz de puntos equidistantes(i,j) en un rango de valores dado (sera distinto dependiendo
     * de la función elegida) y  si hemos elegido la opcion de calcular la función
     * recorre la matriz calculando para cada punto el valor de la función que hemos elegido.
     */
    public static Vector[][] creaMatriz(int n,boolean calcularFuncion) {
        double x0, x1, i, j;
        x0 = -1.5;
        x1 = 2.5;

        Vector[][] matriz = new Vector[n][n];
        for (int f = 0; f < n; f++) {
            for (int c = 0; c < n; c++) { //Crear la matriz de puntos equidistantes
                i = x0 + (double) f * (x1 - x0) / (n - 1);
                j = x0 + (double) c * (x1 - x0) / (n - 1);

                matriz[f][c] = new Vector(i, j);
                if(calcularFuncion){
                    funcion[f][c] = 2*(-sqrt(i*i+j*j)+(cos(j)+sin(i))*sin(j+i)) + 15*(sqrt((i+1)*(i+1)+j*j)-1)/((sqrt((i+1)*(i+1)+j*j)-1)*(sqrt(i*i+j*j)-1)+1);
                }
            }
        }
        return (matriz);
    }

    /**
     * Función que dado un punto y la matriz devuelve el valor de la función en ese punto
     * @param matriz
     * @param x
     * @param y
     * @return puntoFuncion
     */
    public static double calculaFuncionPunto(Vector[][] matriz,int x, int y){
        double i = matriz[x][y].x; //MATRIZ.FUNCION[X]
        double j = matriz[x][y].y;
        double puntoFuncion = 2*(-sqrt(i*i+j*j)+(cos(j)+sin(i))*sin(j+i)) + 15*(sqrt((i+1)*(i+1)+j*j)-1)/((sqrt((i+1)*(i+1)+j*j)-1)*(sqrt(i*i+j*j)-1)+1);

        return puntoFuncion;
    }

    /**
     * @param n Dimension de la matriz.
     * @param matriz Matriz en la que está representada la función.
     * @param random Número aleatorio creado con semilla para generar el punto de comienzo.
     * @param activarBucles si es true salimos del metodo cuando encontramos un metodo ya recorrido
     * @param calcularFuncion si es false calcula el valor de la función
     * @param recorrido matriz de boolena que guarda los puntos por los que hemos pasado para salir de la función
     *                  en caso de toparnos con un camino ya recorrido.
     * @return recorrido
     *
     * Función que escoge un punto de comienzo aleatorio y mira el valor de los 4 puntos colindantes de la matriz
     * y avanza al que tenga mayor valor. Si el valor en el punto actual es mayor o igual que los colinantes termina.
     */
     public static boolean[][] algoritmoVoraz(int n, Vector[][] matriz, Random random, boolean activarBucles,boolean calcularFuncion,boolean[][]recorrido){
        int x = random.nextInt(n - 1);
        int y = random.nextInt(n - 1);
        int xMax = 0;
        int yMax = 0;

        if(!calcularFuncion){
            //calculamos función en punto inicial si no lo hemos calculado ya
            if(funcion[x][y]==0) {
                funcion[x][y] = calculaFuncionPunto(matriz, x, y);
            }
        }

        if (activarBucles && recorrido[x][y]) {
            return recorrido; //si ya hemos pasado por ese punto termina
        }
        recorrido[x][y] = true;

        while (true) {
            //ver cual es el elemento mayor de los colindantes
            for (int f = -1; f <= 1; f++) {
                for (int c = -1; c <= 1; c++) {
                    if ((f != 0 && c != 0) || 0 > x + f || x + f >= n || 0 > y + c || y + c >= n || (f == 0 && c == 0))
                        continue;
                    if(!calcularFuncion){ //Calculamos función en puntos colindantes si no la hemos calculado ya
                        if(funcion[x+f][y+c]==0){
                            funcion[x+f][y+c] = calculaFuncionPunto(matriz,x+f,y+c);
                        }
                    }
                    if (funcion[x+f][y+c] > funcion[xMax][yMax]) { //vemos cual es el mayor
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
            if(recorrido[x][y] && activarBucles) { //si ya hemos pasado por alguno de los puntos del recorrido termina
                return recorrido;
            }
            recorrido[x][y]= true;
        }
        maxRecorrido = funcion[x][y];
        return recorrido;
    }

    /**
     * Función que ejecuta el main tantas veces como le digamos y calcula el tiempo de busqueda
     * y el de inserción
     * @param p
     * @param semilla
     * @param n
     * @param activarBucles
     * @param calcularFuncion
     */
    public static void ejecutaMain(int p, int semilla, int n, boolean activarBucles,boolean calcularFuncion){
        funcion = new double[n][n];
        long t0, tInic, tBusq;
        t0 = System.nanoTime();
        Vector[][] matriz = creaMatriz(n,calcularFuncion); //crea matriz y calcula la función si hemos elegido esa opcion(funcion es una variable global)
        tInic = System.nanoTime() - t0;

        t0 = System.nanoTime();
        //Calcular máximo de la función con el algoritmo probabilistico (si p=1 algoritmo voraz)
        double maxFuncion=0;
        Random random = new Random(semilla);
        boolean[][] recorrido = new boolean[n][n];
        for (int k = 0; k < p; k++) {
            recorrido = algoritmoVoraz(n,matriz,random,activarBucles,calcularFuncion,recorrido);
            if(maxRecorrido>maxFuncion) maxFuncion = maxRecorrido;
        }
        tBusq = System.nanoTime() - t0;

        System.out.printf("%.5f seg. %.5f seg.\n", 1e-9 * tInic ,1e-9 * tBusq);

        tTotalBusq = tTotalBusq + tBusq;
        tTotalInic = tTotalInic + tInic;
        //System.out.println("Maximo funcion:"+ maxFuncion);
    }

    public static void main(String[] args){
        int nEjecuciones = 10;

        //PARAMETROS DE ENTRADA
        int p = 50000; //Numero de veces que realizamos el algoritmo voraz.
        int semilla; //Semilla para generar numeros aleatorios
        n = 2000; //Dimension de la matriz DECLARADO COMO VARIABLE GLOBAL
        boolean activarBucles = true; //Si es true activa la opción de parar cuando encuentre un camino ya recorrido
        boolean calcularFuncion = true; //Si es true la función se calcula al inicializar la matriz
        //si es false se calcula según se va necesitando en el algoritmo

        System.out.println("FuncionTarea2. Lanzamientos del alg. voraz: " + p + " - Dimensión: " + n);
        System.out.println("Activar bucles: " + activarBucles + " - Calcular funcion: " + calcularFuncion);
        System.out.println("Numero ejecuciones: " + nEjecuciones);
        System.out.println("Tiempo inicialización - Tiempo busqueda");
        for (int i = 0; i < nEjecuciones; i++) {
            semilla = i;
            ejecutaMain(p,semilla,n,activarBucles,calcularFuncion);
        }
        System.out.printf("Media tiempo inicializacion: %.5f\n", 1e-9 * tTotalInic/nEjecuciones);
        System.out.printf("Media tiempo busqueda: %.5f\n", 1e-9 * tTotalBusq/nEjecuciones);
        long tTotal = (tTotalBusq/nEjecuciones)+(tTotalInic/nEjecuciones);
        System.out.printf("Tiempo total: %.5f\n", 1e-9 * tTotal);

    }

}