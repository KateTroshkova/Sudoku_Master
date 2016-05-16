package com.itschool.itprogect.sudoku_master;

import android.content.Context;
import android.widget.Toast;

public class Solve {

    private final int size=9;
    private final int boxSize=3;

    public Solve(int matrix[][], Context context) {
        boolean hasSolution=true;
        for(int i=0; i<size; i++){
            for (int j=0; j<size; j++){
                if (matrix[i][j]!=0 && !legal(i, j, matrix[i][j], matrix) && hasSolution) {
                    error(context);
                    hasSolution=false;
                }
            }
        }
        if (hasSolution){
            if (solve(0,0,matrix)) print(matrix);
            else error(context);
        }
    }

    private boolean solve(int i, int j, int[][] matrix) {
        //если ряд последний, проверяем следующую колонку
        //если колонка последняя, значит судоку решен
        if (i == size) {
            i = 0;
            if (++j == size)
                return true;
        }
        //если позиция уже занята, следующий шаг
        if (matrix[i][j] != 0) return solve(i+1, j, matrix);
        //подбирает возможное значение, если конфликтов нет, проверяет следующие шаги
        for (int val = 1; val <= 9; ++val) {
            if (legal(i, j, val, matrix)) {
                matrix[i][j] = val;
                if (solve(i+1, j, matrix)) return true;
            }
        }
        //если в одной из рекурсивных проверок не будет возможности поставить цифру
        //алгоритм возвращается в последнее место, где конфликтов не было
        //и пытается подобрать другие возможные значения
        //если снова возникает конфликт, возвращается в предыдущее "подходящее" место
        matrix[i][j] = 0;
        return false;
        }

    private boolean legal(int i, int j, int value, int[][] matrix) {
        //проеряет, можно ли поставить цифру
        //если значение есть в заданной колонке в любом из рядов
        //или значение есть в заданном ряду в любой из колонок
        //или значение есть в квадрате
        //цифры на заданном месте быть не может

        //проверка колонки
        for (int k = 0; k < size; ++k){
            if (value == matrix[k][j] && k!=i) return false;
        }
        //проверка ряда
        for (int k = 0; k < size; ++k){
            if (value == matrix[i][k] && k!=j) return false;
        }
        //проверка квадрата
        int rowStart = (i / boxSize)*3;
        int columnStart = (j / boxSize)*3;
        for (int k = 0; k < boxSize; ++k){
            for (int m = 0; m < boxSize; ++m){
                if (value == matrix[rowStart+k][columnStart+m] && rowStart+k!=i && columnStart+m!=j) return false;
            }
        }
        return true;
    }

    private void print(int[][] solution) {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                MainActivity.field[i][j].setText(Integer.toString(solution[i][j]));
            }
        }
    }

    private void error(Context context){
        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_LONG).show();
    }
}
