
package com.lazydog.lavalamp;
//
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

//
import android.util.Log;

import java.nio.FloatBuffer;




public class Marching {
public static final String TAG = "Marching";

/* Indexing convention:

             Vertices:                    Edges:

          4  ______________ 5           ______________
           /|            /|           /|     4      /|
          / |         6 / |       7  / |8        5 / |
      7  /_____________/  |        /______________/  | 9
        |   |         |   |        |   |   6     |   |
        | 0 |_________|___| 1      |   |_________|10_|
        |  /          |  /      11 | 3/     0    |  /
        | /           | /          | /           | / 1
      3 |/____________|/ 2         |/____________|/
                                          2
 */

public static final int edgeTable[] = {
  0x0  , 0x109, 0x203, 0x30a, 0x406, 0x50f, 0x605, 0x70c,
  0x80c, 0x905, 0xa0f, 0xb06, 0xc0a, 0xd03, 0xe09, 0xf00,
  0x190, 0x99 , 0x393, 0x29a, 0x596, 0x49f, 0x795, 0x69c,
  0x99c, 0x895, 0xb9f, 0xa96, 0xd9a, 0xc93, 0xf99, 0xe90,
  0x230, 0x339, 0x33 , 0x13a, 0x636, 0x73f, 0x435, 0x53c,
  0xa3c, 0xb35, 0x83f, 0x936, 0xe3a, 0xf33, 0xc39, 0xd30,
  0x3a0, 0x2a9, 0x1a3, 0xaa , 0x7a6, 0x6af, 0x5a5, 0x4ac,
  0xbac, 0xaa5, 0x9af, 0x8a6, 0xfaa, 0xea3, 0xda9, 0xca0,
  0x460, 0x569, 0x663, 0x76a, 0x66 , 0x16f, 0x265, 0x36c,
  0xc6c, 0xd65, 0xe6f, 0xf66, 0x86a, 0x963, 0xa69, 0xb60,
  0x5f0, 0x4f9, 0x7f3, 0x6fa, 0x1f6, 0xff , 0x3f5, 0x2fc,
  0xdfc, 0xcf5, 0xfff, 0xef6, 0x9fa, 0x8f3, 0xbf9, 0xaf0,
  0x650, 0x759, 0x453, 0x55a, 0x256, 0x35f, 0x55 , 0x15c,
  0xe5c, 0xf55, 0xc5f, 0xd56, 0xa5a, 0xb53, 0x859, 0x950,
  0x7c0, 0x6c9, 0x5c3, 0x4ca, 0x3c6, 0x2cf, 0x1c5, 0xcc ,
  0xfcc, 0xec5, 0xdcf, 0xcc6, 0xbca, 0xac3, 0x9c9, 0x8c0,
  0x8c0, 0x9c9, 0xac3, 0xbca, 0xcc6, 0xdcf, 0xec5, 0xfcc,
  0xcc , 0x1c5, 0x2cf, 0x3c6, 0x4ca, 0x5c3, 0x6c9, 0x7c0,
  0x950, 0x859, 0xb53, 0xa5a, 0xd56, 0xc5f, 0xf55, 0xe5c,
  0x15c, 0x55 , 0x35f, 0x256, 0x55a, 0x453, 0x759, 0x650,
  0xaf0, 0xbf9, 0x8f3, 0x9fa, 0xef6, 0xfff, 0xcf5, 0xdfc,
  0x2fc, 0x3f5, 0xff , 0x1f6, 0x6fa, 0x7f3, 0x4f9, 0x5f0,
  0xb60, 0xa69, 0x963, 0x86a, 0xf66, 0xe6f, 0xd65, 0xc6c,
  0x36c, 0x265, 0x16f, 0x66 , 0x76a, 0x663, 0x569, 0x460,
  0xca0, 0xda9, 0xea3, 0xfaa, 0x8a6, 0x9af, 0xaa5, 0xbac,
  0x4ac, 0x5a5, 0x6af, 0x7a6, 0xaa , 0x1a3, 0x2a9, 0x3a0,
  0xd30, 0xc39, 0xf33, 0xe3a, 0x936, 0x83f, 0xb35, 0xa3c,
  0x53c, 0x435, 0x73f, 0x636, 0x13a, 0x33 , 0x339, 0x230,
  0xe90, 0xf99, 0xc93, 0xd9a, 0xa96, 0xb9f, 0x895, 0x99c,
  0x69c, 0x795, 0x49f, 0x596, 0x29a, 0x393, 0x99 , 0x190,
  0xf00, 0xe09, 0xd03, 0xc0a, 0xb06, 0xa0f, 0x905, 0x80c,
  0x70c, 0x605, 0x50f, 0x406, 0x30a, 0x203, 0x109, 0x0
};


public static final int triTable[][] = {
  {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  1,  9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  8,  3,  9,  8,  1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  3,  1,  2, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 9,  2, 10,  0,  2,  9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 2,  8,  3,  2, 10,  8, 10,  9,  8, -1, -1, -1, -1, -1, -1, -1},
  { 3, 11,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0, 11,  2,  8, 11,  0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  9,  0,  2,  3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1, 11,  2,  1,  9, 11,  9,  8, 11, -1, -1, -1, -1, -1, -1, -1},
  { 3, 10,  1, 11, 10,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0, 10,  1,  0,  8, 10,  8, 11, 10, -1, -1, -1, -1, -1, -1, -1},
  { 3,  9,  0,  3, 11,  9, 11, 10,  9, -1, -1, -1, -1, -1, -1, -1},
  { 9,  8, 10, 10,  8, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  7,  8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  3,  0,  7,  3,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  1,  9,  8,  4,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  1,  9,  4,  7,  1,  7,  3,  1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 10,  8,  4,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 3,  4,  7,  3,  0,  4,  1,  2, 10, -1, -1, -1, -1, -1, -1, -1},
  { 9,  2, 10,  9,  0,  2,  8,  4,  7, -1, -1, -1, -1, -1, -1, -1},
  { 2, 10,  9,  2,  9,  7,  2,  7,  3,  7,  9,  4, -1, -1, -1, -1},
  { 8,  4,  7,  3, 11,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {11,  4,  7, 11,  2,  4,  2,  0,  4, -1, -1, -1, -1, -1, -1, -1},
  { 9,  0,  1,  8,  4,  7,  2,  3, 11, -1, -1, -1, -1, -1, -1, -1},
  { 4,  7, 11,  9,  4, 11,  9, 11,  2,  9,  2,  1, -1, -1, -1, -1},
  { 3, 10,  1,  3, 11, 10,  7,  8,  4, -1, -1, -1, -1, -1, -1, -1},
  { 1, 11, 10,  1,  4, 11,  1,  0,  4,  7, 11,  4, -1, -1, -1, -1},
  { 4,  7,  8,  9,  0, 11,  9, 11, 10, 11,  0,  3, -1, -1, -1, -1},
  { 4,  7, 11,  4, 11,  9,  9, 11, 10, -1, -1, -1, -1, -1, -1, -1},
  { 9,  5,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 9,  5,  4,  0,  8,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  5,  4,  1,  5,  0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 8,  5,  4,  8,  3,  5,  3,  1,  5, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 10,  9,  5,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 3,  0,  8,  1,  2, 10,  4,  9,  5, -1, -1, -1, -1, -1, -1, -1},
  { 5,  2, 10,  5,  4,  2,  4,  0,  2, -1, -1, -1, -1, -1, -1, -1},
  { 2, 10,  5,  3,  2,  5,  3,  5,  4,  3,  4,  8, -1, -1, -1, -1},
  { 9,  5,  4,  2,  3, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0, 11,  2,  0,  8, 11,  4,  9,  5, -1, -1, -1, -1, -1, -1, -1},
  { 0,  5,  4,  0,  1,  5,  2,  3, 11, -1, -1, -1, -1, -1, -1, -1},
  { 2,  1,  5,  2,  5,  8,  2,  8, 11,  4,  8,  5, -1, -1, -1, -1},
  {10,  3, 11, 10,  1,  3,  9,  5,  4, -1, -1, -1, -1, -1, -1, -1},
  { 4,  9,  5,  0,  8,  1,  8, 10,  1,  8, 11, 10, -1, -1, -1, -1},
  { 5,  4,  0,  5,  0, 11,  5, 11, 10, 11,  0,  3, -1, -1, -1, -1},
  { 5,  4,  8,  5,  8, 10, 10,  8, 11, -1, -1, -1, -1, -1, -1, -1},
  { 9,  7,  8,  5,  7,  9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 9,  3,  0,  9,  5,  3,  5,  7,  3, -1, -1, -1, -1, -1, -1, -1},
  { 0,  7,  8,  0,  1,  7,  1,  5,  7, -1, -1, -1, -1, -1, -1, -1},
  { 1,  5,  3,  3,  5,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 9,  7,  8,  9,  5,  7, 10,  1,  2, -1, -1, -1, -1, -1, -1, -1},
  {10,  1,  2,  9,  5,  0,  5,  3,  0,  5,  7,  3, -1, -1, -1, -1},
  { 8,  0,  2,  8,  2,  5,  8,  5,  7, 10,  5,  2, -1, -1, -1, -1},
  { 2, 10,  5,  2,  5,  3,  3,  5,  7, -1, -1, -1, -1, -1, -1, -1},
  { 7,  9,  5,  7,  8,  9,  3, 11,  2, -1, -1, -1, -1, -1, -1, -1},
  { 9,  5,  7,  9,  7,  2,  9,  2,  0,  2,  7, 11, -1, -1, -1, -1},
  { 2,  3, 11,  0,  1,  8,  1,  7,  8,  1,  5,  7, -1, -1, -1, -1},
  {11,  2,  1, 11,  1,  7,  7,  1,  5, -1, -1, -1, -1, -1, -1, -1},
  { 9,  5,  8,  8,  5,  7, 10,  1,  3, 10,  3, 11, -1, -1, -1, -1},
  { 5,  7,  0,  5,  0,  9,  7, 11,  0,  1,  0, 10, 11, 10,  0, -1},
  {11, 10,  0, 11,  0,  3, 10,  5,  0,  8,  0,  7,  5,  7,  0, -1},
  {11, 10,  5,  7, 11,  5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {10,  6,  5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  3,  5, 10,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 9,  0,  1,  5, 10,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  8,  3,  1,  9,  8,  5, 10,  6, -1, -1, -1, -1, -1, -1, -1},
  { 1,  6,  5,  2,  6,  1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  6,  5,  1,  2,  6,  3,  0,  8, -1, -1, -1, -1, -1, -1, -1},
  { 9,  6,  5,  9,  0,  6,  0,  2,  6, -1, -1, -1, -1, -1, -1, -1},
  { 5,  9,  8,  5,  8,  2,  5,  2,  6,  3,  2,  8, -1, -1, -1, -1},
  { 2,  3, 11, 10,  6,  5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {11,  0,  8, 11,  2,  0, 10,  6,  5, -1, -1, -1, -1, -1, -1, -1},
  { 0,  1,  9,  2,  3, 11,  5, 10,  6, -1, -1, -1, -1, -1, -1, -1},
  { 5, 10,  6,  1,  9,  2,  9, 11,  2,  9,  8, 11, -1, -1, -1, -1},
  { 6,  3, 11,  6,  5,  3,  5,  1,  3, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8, 11,  0, 11,  5,  0,  5,  1,  5, 11,  6, -1, -1, -1, -1},
  { 3, 11,  6,  0,  3,  6,  0,  6,  5,  0,  5,  9, -1, -1, -1, -1},
  { 6,  5,  9,  6,  9, 11, 11,  9,  8, -1, -1, -1, -1, -1, -1, -1},
  { 5, 10,  6,  4,  7,  8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  3,  0,  4,  7,  3,  6,  5, 10, -1, -1, -1, -1, -1, -1, -1},
  { 1,  9,  0,  5, 10,  6,  8,  4,  7, -1, -1, -1, -1, -1, -1, -1},
  {10,  6,  5,  1,  9,  7,  1,  7,  3,  7,  9,  4, -1, -1, -1, -1},
  { 6,  1,  2,  6,  5,  1,  4,  7,  8, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2,  5,  5,  2,  6,  3,  0,  4,  3,  4,  7, -1, -1, -1, -1},
  { 8,  4,  7,  9,  0,  5,  0,  6,  5,  0,  2,  6, -1, -1, -1, -1},
  { 7,  3,  9,  7,  9,  4,  3,  2,  9,  5,  9,  6,  2,  6,  9, -1},
  { 3, 11,  2,  7,  8,  4, 10,  6,  5, -1, -1, -1, -1, -1, -1, -1},
  { 5, 10,  6,  4,  7,  2,  4,  2,  0,  2,  7, 11, -1, -1, -1, -1},
  { 0,  1,  9,  4,  7,  8,  2,  3, 11,  5, 10,  6, -1, -1, -1, -1},
  { 9,  2,  1,  9, 11,  2,  9,  4, 11,  7, 11,  4,  5, 10,  6, -1},
  { 8,  4,  7,  3, 11,  5,  3,  5,  1,  5, 11,  6, -1, -1, -1, -1},
  { 5,  1, 11,  5, 11,  6,  1,  0, 11,  7, 11,  4,  0,  4, 11, -1},
  { 0,  5,  9,  0,  6,  5,  0,  3,  6, 11,  6,  3,  8,  4,  7, -1},
  { 6,  5,  9,  6,  9, 11,  4,  7,  9,  7, 11,  9, -1, -1, -1, -1},
  {10,  4,  9,  6,  4, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4, 10,  6,  4,  9, 10,  0,  8,  3, -1, -1, -1, -1, -1, -1, -1},
  {10,  0,  1, 10,  6,  0,  6,  4,  0, -1, -1, -1, -1, -1, -1, -1},
  { 8,  3,  1,  8,  1,  6,  8,  6,  4,  6,  1, 10, -1, -1, -1, -1},
  { 1,  4,  9,  1,  2,  4,  2,  6,  4, -1, -1, -1, -1, -1, -1, -1},
  { 3,  0,  8,  1,  2,  9,  2,  4,  9,  2,  6,  4, -1, -1, -1, -1},
  { 0,  2,  4,  4,  2,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 8,  3,  2,  8,  2,  4,  4,  2,  6, -1, -1, -1, -1, -1, -1, -1},
  {10,  4,  9, 10,  6,  4, 11,  2,  3, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  2,  2,  8, 11,  4,  9, 10,  4, 10,  6, -1, -1, -1, -1},
  { 3, 11,  2,  0,  1,  6,  0,  6,  4,  6,  1, 10, -1, -1, -1, -1},
  { 6,  4,  1,  6,  1, 10,  4,  8,  1,  2,  1, 11,  8, 11,  1, -1},
  { 9,  6,  4,  9,  3,  6,  9,  1,  3, 11,  6,  3, -1, -1, -1, -1},
  { 8, 11,  1,  8,  1,  0, 11,  6,  1,  9,  1,  4,  6,  4,  1, -1},
  { 3, 11,  6,  3,  6,  0,  0,  6,  4, -1, -1, -1, -1, -1, -1, -1},
  { 6,  4,  8, 11,  6,  8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 7, 10,  6,  7,  8, 10,  8,  9, 10, -1, -1, -1, -1, -1, -1, -1},
  { 0,  7,  3,  0, 10,  7,  0,  9, 10,  6,  7, 10, -1, -1, -1, -1},
  {10,  6,  7,  1, 10,  7,  1,  7,  8,  1,  8,  0, -1, -1, -1, -1},
  {10,  6,  7, 10,  7,  1,  1,  7,  3, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2,  6,  1,  6,  8,  1,  8,  9,  8,  6,  7, -1, -1, -1, -1},
  { 2,  6,  9,  2,  9,  1,  6,  7,  9,  0,  9,  3,  7,  3,  9, -1},
  { 7,  8,  0,  7,  0,  6,  6,  0,  2, -1, -1, -1, -1, -1, -1, -1},
  { 7,  3,  2,  6,  7,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 2,  3, 11, 10,  6,  8, 10,  8,  9,  8,  6,  7, -1, -1, -1, -1},
  { 2,  0,  7,  2,  7, 11,  0,  9,  7,  6,  7, 10,  9, 10,  7, -1},
  { 1,  8,  0,  1,  7,  8,  1, 10,  7,  6,  7, 10,  2,  3, 11, -1},
  {11,  2,  1, 11,  1,  7, 10,  6,  1,  6,  7,  1, -1, -1, -1, -1},
  { 8,  9,  6,  8,  6,  7,  9,  1,  6, 11,  6,  3,  1,  3,  6, -1},
  { 0,  9,  1, 11,  6,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 7,  8,  0,  7,  0,  6,  3, 11,  0, 11,  6,  0, -1, -1, -1, -1},
  { 7, 11,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 7,  6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 3,  0,  8, 11,  7,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  1,  9, 11,  7,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 8,  1,  9,  8,  3,  1, 11,  7,  6, -1, -1, -1, -1, -1, -1, -1},
  {10,  1,  2,  6, 11,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 10,  3,  0,  8,  6, 11,  7, -1, -1, -1, -1, -1, -1, -1},
  { 2,  9,  0,  2, 10,  9,  6, 11,  7, -1, -1, -1, -1, -1, -1, -1},
  { 6, 11,  7,  2, 10,  3, 10,  8,  3, 10,  9,  8, -1, -1, -1, -1},
  { 7,  2,  3,  6,  2,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 7,  0,  8,  7,  6,  0,  6,  2,  0, -1, -1, -1, -1, -1, -1, -1},
  { 2,  7,  6,  2,  3,  7,  0,  1,  9, -1, -1, -1, -1, -1, -1, -1},
  { 1,  6,  2,  1,  8,  6,  1,  9,  8,  8,  7,  6, -1, -1, -1, -1},
  {10,  7,  6, 10,  1,  7,  1,  3,  7, -1, -1, -1, -1, -1, -1, -1},
  {10,  7,  6,  1,  7, 10,  1,  8,  7,  1,  0,  8, -1, -1, -1, -1},
  { 0,  3,  7,  0,  7, 10,  0, 10,  9,  6, 10,  7, -1, -1, -1, -1},
  { 7,  6, 10,  7, 10,  8,  8, 10,  9, -1, -1, -1, -1, -1, -1, -1},
  { 6,  8,  4, 11,  8,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 3,  6, 11,  3,  0,  6,  0,  4,  6, -1, -1, -1, -1, -1, -1, -1},
  { 8,  6, 11,  8,  4,  6,  9,  0,  1, -1, -1, -1, -1, -1, -1, -1},
  { 9,  4,  6,  9,  6,  3,  9,  3,  1, 11,  3,  6, -1, -1, -1, -1},
  { 6,  8,  4,  6, 11,  8,  2, 10,  1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 10,  3,  0, 11,  0,  6, 11,  0,  4,  6, -1, -1, -1, -1},
  { 4, 11,  8,  4,  6, 11,  0,  2,  9,  2, 10,  9, -1, -1, -1, -1},
  {10,  9,  3, 10,  3,  2,  9,  4,  3, 11,  3,  6,  4,  6,  3, -1},
  { 8,  2,  3,  8,  4,  2,  4,  6,  2, -1, -1, -1, -1, -1, -1, -1},
  { 0,  4,  2,  4,  6,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  9,  0,  2,  3,  4,  2,  4,  6,  4,  3,  8, -1, -1, -1, -1},
  { 1,  9,  4,  1,  4,  2,  2,  4,  6, -1, -1, -1, -1, -1, -1, -1},
  { 8,  1,  3,  8,  6,  1,  8,  4,  6,  6, 10,  1, -1, -1, -1, -1},
  {10,  1,  0, 10,  0,  6,  6,  0,  4, -1, -1, -1, -1, -1, -1, -1},
  { 4,  6,  3,  4,  3,  8,  6, 10,  3,  0,  3,  9, 10,  9,  3, -1},
  {10,  9,  4,  6, 10,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  9,  5,  7,  6, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  3,  4,  9,  5, 11,  7,  6, -1, -1, -1, -1, -1, -1, -1},
  { 5,  0,  1,  5,  4,  0,  7,  6, 11, -1, -1, -1, -1, -1, -1, -1},
  {11,  7,  6,  8,  3,  4,  3,  5,  4,  3,  1,  5, -1, -1, -1, -1},
  { 9,  5,  4, 10,  1,  2,  7,  6, 11, -1, -1, -1, -1, -1, -1, -1},
  { 6, 11,  7,  1,  2, 10,  0,  8,  3,  4,  9,  5, -1, -1, -1, -1},
  { 7,  6, 11,  5,  4, 10,  4,  2, 10,  4,  0,  2, -1, -1, -1, -1},
  { 3,  4,  8,  3,  5,  4,  3,  2,  5, 10,  5,  2, 11,  7,  6, -1},
  { 7,  2,  3,  7,  6,  2,  5,  4,  9, -1, -1, -1, -1, -1, -1, -1},
  { 9,  5,  4,  0,  8,  6,  0,  6,  2,  6,  8,  7, -1, -1, -1, -1},
  { 3,  6,  2,  3,  7,  6,  1,  5,  0,  5,  4,  0, -1, -1, -1, -1},
  { 6,  2,  8,  6,  8,  7,  2,  1,  8,  4,  8,  5,  1,  5,  8, -1},
  { 9,  5,  4, 10,  1,  6,  1,  7,  6,  1,  3,  7, -1, -1, -1, -1},
  { 1,  6, 10,  1,  7,  6,  1,  0,  7,  8,  7,  0,  9,  5,  4, -1},
  { 4,  0, 10,  4, 10,  5,  0,  3, 10,  6, 10,  7,  3,  7, 10, -1},
  { 7,  6, 10,  7, 10,  8,  5,  4, 10,  4,  8, 10, -1, -1, -1, -1},
  { 6,  9,  5,  6, 11,  9, 11,  8,  9, -1, -1, -1, -1, -1, -1, -1},
  { 3,  6, 11,  0,  6,  3,  0,  5,  6,  0,  9,  5, -1, -1, -1, -1},
  { 0, 11,  8,  0,  5, 11,  0,  1,  5,  5,  6, 11, -1, -1, -1, -1},
  { 6, 11,  3,  6,  3,  5,  5,  3,  1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 10,  9,  5, 11,  9, 11,  8, 11,  5,  6, -1, -1, -1, -1},
  { 0, 11,  3,  0,  6, 11,  0,  9,  6,  5,  6,  9,  1,  2, 10, -1},
  {11,  8,  5, 11,  5,  6,  8,  0,  5, 10,  5,  2,  0,  2,  5, -1},
  { 6, 11,  3,  6,  3,  5,  2, 10,  3, 10,  5,  3, -1, -1, -1, -1},
  { 5,  8,  9,  5,  2,  8,  5,  6,  2,  3,  8,  2, -1, -1, -1, -1},
  { 9,  5,  6,  9,  6,  0,  0,  6,  2, -1, -1, -1, -1, -1, -1, -1},
  { 1,  5,  8,  1,  8,  0,  5,  6,  8,  3,  8,  2,  6,  2,  8, -1},
  { 1,  5,  6,  2,  1,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  3,  6,  1,  6, 10,  3,  8,  6,  5,  6,  9,  8,  9,  6, -1},
  {10,  1,  0, 10,  0,  6,  9,  5,  0,  5,  6,  0, -1, -1, -1, -1},
  { 0,  3,  8,  5,  6, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {10,  5,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {11,  5, 10,  7,  5, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {11,  5, 10, 11,  7,  5,  8,  3,  0, -1, -1, -1, -1, -1, -1, -1},
  { 5, 11,  7,  5, 10, 11,  1,  9,  0, -1, -1, -1, -1, -1, -1, -1},
  {10,  7,  5, 10, 11,  7,  9,  8,  1,  8,  3,  1, -1, -1, -1, -1},
  {11,  1,  2, 11,  7,  1,  7,  5,  1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  3,  1,  2,  7,  1,  7,  5,  7,  2, 11, -1, -1, -1, -1},
  { 9,  7,  5,  9,  2,  7,  9,  0,  2,  2, 11,  7, -1, -1, -1, -1},
  { 7,  5,  2,  7,  2, 11,  5,  9,  2,  3,  2,  8,  9,  8,  2, -1},
  { 2,  5, 10,  2,  3,  5,  3,  7,  5, -1, -1, -1, -1, -1, -1, -1},
  { 8,  2,  0,  8,  5,  2,  8,  7,  5, 10,  2,  5, -1, -1, -1, -1},
  { 9,  0,  1,  5, 10,  3,  5,  3,  7,  3, 10,  2, -1, -1, -1, -1},
  { 9,  8,  2,  9,  2,  1,  8,  7,  2, 10,  2,  5,  7,  5,  2, -1},
  { 1,  3,  5,  3,  7,  5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  7,  0,  7,  1,  1,  7,  5, -1, -1, -1, -1, -1, -1, -1},
  { 9,  0,  3,  9,  3,  5,  5,  3,  7, -1, -1, -1, -1, -1, -1, -1},
  { 9,  8,  7,  5,  9,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 5,  8,  4,  5, 10,  8, 10, 11,  8, -1, -1, -1, -1, -1, -1, -1},
  { 5,  0,  4,  5, 11,  0,  5, 10, 11, 11,  3,  0, -1, -1, -1, -1},
  { 0,  1,  9,  8,  4, 10,  8, 10, 11, 10,  4,  5, -1, -1, -1, -1},
  {10, 11,  4, 10,  4,  5, 11,  3,  4,  9,  4,  1,  3,  1,  4, -1},
  { 2,  5,  1,  2,  8,  5,  2, 11,  8,  4,  5,  8, -1, -1, -1, -1},
  { 0,  4, 11,  0, 11,  3,  4,  5, 11,  2, 11,  1,  5,  1, 11, -1},
  { 0,  2,  5,  0,  5,  9,  2, 11,  5,  4,  5,  8, 11,  8,  5, -1},
  { 9,  4,  5,  2, 11,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 2,  5, 10,  3,  5,  2,  3,  4,  5,  3,  8,  4, -1, -1, -1, -1},
  { 5, 10,  2,  5,  2,  4,  4,  2,  0, -1, -1, -1, -1, -1, -1, -1},
  { 3, 10,  2,  3,  5, 10,  3,  8,  5,  4,  5,  8,  0,  1,  9, -1},
  { 5, 10,  2,  5,  2,  4,  1,  9,  2,  9,  4,  2, -1, -1, -1, -1},
  { 8,  4,  5,  8,  5,  3,  3,  5,  1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  4,  5,  1,  0,  5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 8,  4,  5,  8,  5,  3,  9,  0,  5,  0,  3,  5, -1, -1, -1, -1},
  { 9,  4,  5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4, 11,  7,  4,  9, 11,  9, 10, 11, -1, -1, -1, -1, -1, -1, -1},
  { 0,  8,  3,  4,  9,  7,  9, 11,  7,  9, 10, 11, -1, -1, -1, -1},
  { 1, 10, 11,  1, 11,  4,  1,  4,  0,  7,  4, 11, -1, -1, -1, -1},
  { 3,  1,  4,  3,  4,  8,  1, 10,  4,  7,  4, 11, 10, 11,  4, -1},
  { 4, 11,  7,  9, 11,  4,  9,  2, 11,  9,  1,  2, -1, -1, -1, -1},
  { 9,  7,  4,  9, 11,  7,  9,  1, 11,  2, 11,  1,  0,  8,  3, -1},
  {11,  7,  4, 11,  4,  2,  2,  4,  0, -1, -1, -1, -1, -1, -1, -1},
  {11,  7,  4, 11,  4,  2,  8,  3,  4,  3,  2,  4, -1, -1, -1, -1},
  { 2,  9, 10,  2,  7,  9,  2,  3,  7,  7,  4,  9, -1, -1, -1, -1},
  { 9, 10,  7,  9,  7,  4, 10,  2,  7,  8,  7,  0,  2,  0,  7, -1},
  { 3,  7, 10,  3, 10,  2,  7,  4, 10,  1, 10,  0,  4,  0, 10, -1},
  { 1, 10,  2,  8,  7,  4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  9,  1,  4,  1,  7,  7,  1,  3, -1, -1, -1, -1, -1, -1, -1},
  { 4,  9,  1,  4,  1,  7,  0,  8,  1,  8,  7,  1, -1, -1, -1, -1},
  { 4,  0,  3,  7,  4,  3, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 4,  8,  7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 9, 10,  8, 10, 11,  8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 3,  0,  9,  3,  9, 11, 11,  9, 10, -1, -1, -1, -1, -1, -1, -1},
  { 0,  1, 10,  0, 10,  8,  8, 10, 11, -1, -1, -1, -1, -1, -1, -1},
  { 3,  1, 10, 11,  3, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  2, 11,  1, 11,  9,  9, 11,  8, -1, -1, -1, -1, -1, -1, -1},
  { 3,  0,  9,  3,  9, 11,  1,  2,  9,  2, 11,  9, -1, -1, -1, -1},
  { 0,  2, 11,  8,  0, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 3,  2, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 2,  3,  8,  2,  8, 10, 10,  8,  9, -1, -1, -1, -1, -1, -1, -1},
  { 9, 10,  2,  0,  9,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 2,  3,  8,  2,  8, 10,  0,  1,  8,  1, 10,  8, -1, -1, -1, -1},
  { 1, 10,  2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 1,  3,  8,  9,  1,  8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  9,  1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  { 0,  3,  8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
  {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}
};


/*
// Linearly interpolate the position where an isosurface cuts
 //  an edge between two vertices, each with their own scalar value
*/
public static Point interpVertex (float isolevel, Point p1, Point p2, float valp1, float valp2,Point returnPoint)
{
    float mu;
    //Point p = new Point();

    if (Math.abs(isolevel-valp1) <  0.00001f)
      return(p1);
    else if (Math.abs(isolevel-valp2) < 0.00001f)
      return(p2);
    else if (Math.abs(valp1-valp2) <  0.00001f)
      return(p1);

    mu = (isolevel - valp1) / (valp2 - valp1);
    //p.x = p1.x + mu * (p2.x - p1.x);
    //p.y = p1.y + mu * (p2.y - p1.y);
    //p.z = p1.z + mu * (p2.z - p1.z);

    returnPoint.x = p1.x + mu * (p2.x - p1.x);
    returnPoint.y = p1.y + mu * (p2.y - p1.y);
    returnPoint.z = p1.z + mu * (p2.z - p1.z);

    return(returnPoint);
  }


/*
// Given a grid cell and an isolevel, calculate the triangular
  // facets required to represent the isosurface through the cell.
   //Return the number of triangular facets.
   //`triangles' will be loaded up with the vertices at most 5 triangular facets
.
   //0 will be returned if the grid cell is either totally above
   //of totally below the isolevel.
//From the original source:
//GRIDCELL is 8 points, plus 8 "values"
//gridpoints is 8 points, so 24 floats
*/
public static int marchOneCube (GridCell grid, float isolevel, Triangle[] triangles, Point[] vertlist, Point[] vertpool)
{
  int i, ntriang;
  int cubeindex;
  
  //Point[] vertlist = new Point[12]; //TODO:optimize this?


//    Determine the index into the edge table which
  //  tells us which vertices are inside of the surface
  cubeindex = 0;
  if (grid.val[0] < isolevel) cubeindex |= 1;
  if (grid.val[1] < isolevel) cubeindex |= 2;
  if (grid.val[2] < isolevel) cubeindex |= 4;
  if (grid.val[3] < isolevel) cubeindex |= 8;
  if (grid.val[4] < isolevel) cubeindex |= 16;
  if (grid.val[5] < isolevel) cubeindex |= 32;
  if (grid.val[6] < isolevel) cubeindex |= 64;
  if (grid.val[7] < isolevel) cubeindex |= 128;


  //Log("Marching", "marchOneCube: cubeindex="+cubeindex);
  //Log("Marching", "marchOneCube: gridcell's values are: "+grid.val[0]+","+grid.val[1]+","+grid.val[2]+","+grid.val[3]+","+grid.val[4]+","+grid.val[5]+","+grid.val[6]+","+grid.val[7]+",");

  // Cube is entirely in/out of the surface 
  if (edgeTable[cubeindex] == 0) {
    //Log("Marching", "Cube is entirely in/out of the surface,bailing...");
    return(0);
  }

  //Log("Marching", "marchOneCube: cubeindex="+cubeindex+" edgeTable[cubeindex]="+edgeTable[cubeindex]);

  int edgeValue = edgeTable[cubeindex];
  // Find the vertices where the surface intersects the cube 
  //if (edgeTable[cubeindex] & (int)1 != 0)
  if ( (int)(edgeValue & 1) != 0)
      vertlist[0]=interpVertex (isolevel,grid.p[0],grid.p[1],grid.val[0],grid.val[1],vertpool[0]);
  if ( (int)(edgeTable[cubeindex] & 2) != 0)
      vertlist[1]=interpVertex(isolevel,grid.p[1],grid.p[2],grid.val[1],grid.val[2],vertpool[1]);
  if ( (int)(edgeTable[cubeindex] & 4) != 0)
      vertlist[2]=interpVertex(isolevel,grid.p[2],grid.p[3],grid.val[2],grid.val[3],vertpool[2]);
  if ( (int)(edgeTable[cubeindex] & 8) != 0)
      vertlist[3]=interpVertex(isolevel,grid.p[3],grid.p[0],grid.val[3],grid.val[0],vertpool[3]);
  if ( (int)(edgeTable[cubeindex] & 16) != 0)
      vertlist[4]=interpVertex(isolevel,grid.p[4],grid.p[5],grid.val[4],grid.val[5],vertpool[4]);
  if ( (int)(edgeTable[cubeindex] & 32) != 0)
      vertlist[5]=interpVertex(isolevel,grid.p[5],grid.p[6],grid.val[5],grid.val[6],vertpool[5]);
  if ( (int)(edgeTable[cubeindex] & 64) != 0)
      vertlist[6]=interpVertex(isolevel,grid.p[6],grid.p[7],grid.val[6],grid.val[7],vertpool[6]);
  if ( (int)(edgeTable[cubeindex] & 128) != 0)
      vertlist[7]=interpVertex(isolevel,grid.p[7],grid.p[4],grid.val[7],grid.val[4],vertpool[7]);
  if ( (int)(edgeTable[cubeindex] & 256) != 0)
      vertlist[8]=interpVertex(isolevel,grid.p[0],grid.p[4],grid.val[0],grid.val[4],vertpool[8]);
  if ( (int)(edgeTable[cubeindex] & 512) != 0)
      vertlist[9]=interpVertex(isolevel,grid.p[1],grid.p[5],grid.val[1],grid.val[5],vertpool[9]);
  if ( (int)(edgeTable[cubeindex] & 1024) != 0)
      vertlist[10]=interpVertex(isolevel,grid.p[2],grid.p[6],grid.val[2],grid.val[6],vertpool[10]);
  if ( (int)(edgeTable[cubeindex] & 2048) != 0)
      vertlist[11]=interpVertex(isolevel,grid.p[3],grid.p[7],grid.val[3],grid.val[7],vertpool[11]);

// Create the triangle
  //Log("Marching", "triTable[cubeindex][3]=="+triTable[cubeindex][3]);


  ntriang = 0;
  for (i=0; triTable[cubeindex][i] != -1; i+=3)
    {
      //Log("Maching","got a triangle triangles[ntriang]="+triangles[ntriang]);
      triangles[ntriang].p[0] = vertlist[triTable[cubeindex][i  ]];
      triangles[ntriang].p[1] = vertlist[triTable[cubeindex][i+1]];
      triangles[ntriang].p[2] = vertlist[triTable[cubeindex][i+2]];
      ntriang++;
    }

  return(ntriang);
}


/*
// Computes the normal of the scalar field at the given point,
  // for vertex normals (as opposed to face normals.)
*/
public static void doFunctionNormal (float x, float y,float z, LavaLamp lamp,float[] normals, int position)
{
                    
  //Point n = new Point();
  float off = 0.5f;
  normals[position] = lamp.objCompute(x-off, y, z) - lamp.objCompute (x+off, y, z);
  normals[position+1] = lamp.objCompute(x, y-off, z) - lamp.objCompute (x, y+off, z);
  normals[position+2] = lamp.objCompute(x, y, z-off) - lamp.objCompute (x, y, z+off);
// normalize (&n);
  //lamp.gl.glNormal3f (n.x, n.y, n.z);
  //lamp.addNormal3f (n.x, n.y, n.z);
  //FloatBuffer normals = lamp.m_NormalData;
  //normals.put(n.x); normals.put(n.y); normals.put(n.z);
  
  
}

public static void doFunctionNormal2 (float x, float y,float z, float[] normals, int position, LavaLamp lamp)
{
  //Point n = new Point();
  float off = 0.5f;
  normals[position] = lamp.objCompute(x-off, y, z) - lamp.objCompute (x+off, y, z);
  normals[position+1] = lamp.objCompute(x, y-off, z) - lamp.objCompute (x, y+off, z);
  normals[position+2] = lamp.objCompute(x, y, z-off) - lamp.objCompute (x, y, z+off);
  
}



// Calculate the unit normal at p given two other points p1,p2 on the
//   surface. The normal points in the direction of p1 crossproduct p2
public static Point calcNormal(Point p, Point p1, Point p2)
{
  Point n = new Point();
  Point pa = new Point();
  Point pb = new Point();

  pa.x = p1.x - p.x;
  pa.y = p1.y - p.y;
  pa.z = p1.z - p.z;
  pb.x = p2.x - p.x;
  pb.y = p2.y - p.y;
  pb.z = p2.z - p.z;
  n.x = pa.y * pb.z - pa.z * pb.y;
  n.y = pa.z * pb.x - pa.x * pb.z;
  n.z = pa.x * pb.y - pa.y * pb.x;
  return (n);
}

// Call glNormal3f() with a normal of the indicated triangle.
public static  void doNormal(float x1, float y1, float z1,
          float x2, float y2, float z2,
          float x3, float y3, float z3,LavaLamp lamp)
{
  Point p1 = new Point();
  Point p2 = new Point();
  Point p3 = new Point();
  Point p = new Point();

  p1.x = x1; p1.y = y1; p1.z = z1;
  p2.x = x2; p2.y = y2; p2.z = z2;
  p3.x = x3; p3.y = y3; p3.z = z3;
  p = calcNormal(p1, p2, p3);
  //lamp.gl.glNormal3f (p.x, p.y, p.z);
}



public static void marchingCubes(MarchingWork work,
        LavaLamp lamp,
        int startZ,
        int endZ)

{

  final int planeSize = LavaLamp.RESOLUTION * LavaLamp.RESOLUTION ;


  int x,y,z;
  int polys = 0;
  //layers = new float[planeSize * 2];


  Triangle tri[] = work.tri;
  GridCell cell = work.cell;
  float layers[] = work.layers;
  //for MarchingOneCube :
  Point vertlist[] = work.vertlist;
  Point vertpool[] = work.vertpool;


  //results of work:
  float vertices[] = work.vertices;
  float normals[] = work.normals;
  int position = 0;

 

  //moved outside of loops (not sure if useful):
  int i, ntri;
  int row,layer0,layer1;

  if (startZ != 0) //need the layer information from startZ-1
  {
    z = startZ-1;
    layer0 = ( (int)(z & 1) == 1 ? planeSize : 0 );
    layer1 = ( (int)(z & 1) == 1 ? 0 : planeSize );



    //Fill in the XY grid on the currently-bottommost layer.
    row=layer1;
    for (y=0;y<LavaLamp.RESOLUTION;y++, row += LavaLamp.RESOLUTION)
    {
      int myCell=row; //variable used to be called 'cell'...
      for (x = 0; x < LavaLamp.RESOLUTION; x++, myCell++)
      {
        layers[myCell] =  lamp.objCompute( (float)x, (float)y, (float)z );
      }
    }
  }


  for ( z=startZ; z< endZ; z++) //TODO: z = zstart to zend
  {

    layer0 = ( (int)(z & 1) == 1 ? planeSize : 0 );
    layer1 = ( (int)(z & 1) == 1 ? 0 : planeSize );
  


    //Fill in the XY grid on the currently-bottommost layer.
    row=layer1;
    for (y=0;y<LavaLamp.RESOLUTION;y++, row += LavaLamp.RESOLUTION)
    {
      int myCell=row; //variable used to be called 'cell'...
      for (x = 0; x < LavaLamp.RESOLUTION; x++, myCell++) 
      {
        layers[myCell] =  lamp.objCompute( (float)x, (float)y, (float)z );
      }
    }
    

    //Now we've completed one layer (an XY slice of Z.)  Now we can
    //     generate the polygons that fill the space between this layer
    //     and the previous one (unless this is the first layer.)
    if (z == 0)  {
//      Log("Marching","marchingCubes: this is the first layer ");
      continue;
    }

       // r+= 0.01f; g+= 0.01f; b+= 0.01f; //makes stripes...

    for (y = 1; y < LavaLamp.RESOLUTION; y += 1)
      for (x = 1; x < LavaLamp.RESOLUTION; x += 1)
      {



        // there has got to be a better way to do this!
        cell.p[0].x = x-1; cell.p[0].y = y-1; cell.p[0].z = z-1;
        cell.p[1].x = x  ; cell.p[1].y = y-1; cell.p[1].z = z-1;
        cell.p[2].x = x  ; cell.p[2].y = y  ; cell.p[2].z = z-1;
        cell.p[3].x = x-1; cell.p[3].y = y  ; cell.p[3].z = z-1;
        cell.p[4].x = x-1; cell.p[4].y = y-1; cell.p[4].z = z  ;
        cell.p[5].x = x  ; cell.p[5].y = y-1; cell.p[5].z = z  ;
        cell.p[6].x = x  ; cell.p[6].y = y  ; cell.p[6].z = z  ;
        cell.p[7].x = x-1; cell.p[7].y = y  ; cell.p[7].z = z  ;

	//for WHICH==0:
        cell.val[0] = layers[layer0+ ((y-1)*LavaLamp.RESOLUTION) + ((x-1))];
        cell.val[1] = layers[layer0+ ((y-1)*LavaLamp.RESOLUTION) + ((x))];
        cell.val[2] = layers[layer0+ ((y)*LavaLamp.RESOLUTION) + ((x))];
        cell.val[3] = layers[layer0+ ((y)*LavaLamp.RESOLUTION) + ((x-1))];

        //for WHICH==1:
        cell.val[4] = layers[layer1+ ((y-1)*LavaLamp.RESOLUTION) + ((x-1))];
        cell.val[5] = layers[layer1+ ((y-1)*LavaLamp.RESOLUTION) + ((x))];
        cell.val[6] = layers[layer1+ ((y)*LavaLamp.RESOLUTION) + ((x))];
        cell.val[7] = layers[layer1+ ((y)*LavaLamp.RESOLUTION) + ((x-1))];

//timePart1 +=  System.currentTimeMillis()-timeTmp;
//timeTmp=System.currentTimeMillis();

        ntri = marchOneCube (cell, LavaLamp.ISOLEVEL, tri, vertlist, vertpool);

//lamp.m_Timer0.time1(); --this side takes slightly longer...

//timePart2 +=  System.currentTimeMillis()-timeTmp;


//lamp.m_Timer1.time0();
        //Log("Marching", "marchingCubes: marchOneCube() finished, ntri="+ntri);
        polys += ntri;
        for (i = 0; i < ntri; i++)
        {
	  //Point 1 of triangle tri-i:
          doFunctionNormal(tri[i].p[0].x, tri[i].p[0].y, tri[i].p[0].z, lamp, normals, position);
          //vertices.put(tri[i].p[0].x); vertices.put(tri[i].p[0].y); vertices.put(tri[i].p[0].z);
          vertices[position] = tri[i].p[0].x; vertices[position+1]=tri[i].p[0].y; vertices[position+2] = tri[i].p[0].z;
          

          //Point 2 of triangle tri-i:
	  doFunctionNormal(tri[i].p[1].x, tri[i].p[1].y, tri[i].p[1].z, lamp, normals, position+3);
           //vertices.put(tri[i].p[1].x); vertices.put(tri[i].p[1].y); vertices.put(tri[i].p[1].z);
          vertices[position+3] = tri[i].p[1].x; vertices[position+4]=tri[i].p[1].y; vertices[position+5] = tri[i].p[1].z;


          //Point 3 of triangle tri-i:
          doFunctionNormal(tri[i].p[2].x, tri[i].p[2].y, tri[i].p[2].z, lamp, normals, position+6);
           //vertices.put(tri[i].p[2].x); vertices.put(tri[i].p[2].y); vertices.put(tri[i].p[2].z);
          vertices[position+6] = tri[i].p[2].x; vertices[position+7]=tri[i].p[2].y; vertices[position+8] = tri[i].p[2].z;

	  position+=9;

/*
          colors.put(r); colors.put(g); colors.put(b); colors.put(0.0001f); 
          colors.put(r); colors.put(g); colors.put(b); colors.put(0.0001f); 
          colors.put(r); colors.put(g); colors.put(b); colors.put(0.0001f); 
*/
      
	} /* for i = ... */


//lamp.m_Timer1.time1();

      }

//Log.v(TAG , "timePart1="+timePart1+" timePart2="+timePart2);

  //if (polygon_count)     *polygon_count = polys;

  }  /* for z = ...  */


  work.nVertices = polys * 3;
  work.nCount = position;



//
} /* marchingCubes() */



//XXX -- DEBUGGING
private static void Log(String s, String msg)
{
  System.out.println(s+": "+msg);
}


} //public class Marching { ... } 



/*

// Linearly interpolate the position where an isosurface cuts
 //  an edge between two vertices, each with their own scalar value
static XYZ
interp_vertex (double isolevel, XYZ p1, XYZ p2, double valp1, double valp2)
{
  double mu;
  XYZ p;

  if (ABS(isolevel-valp1) < 0.00001)
    return(p1);
  if (ABS(isolevel-valp2) < 0.00001)
    return(p2);
  if (ABS(valp1-valp2) < 0.00001)
    return(p1);
  mu = (isolevel - valp1) / (valp2 - valp1);
  p.x = p1.x + mu * (p2.x - p1.x);
  p.y = p1.y + mu * (p2.y - p1.y);
  p.z = p1.z + mu * (p2.z - p1.z);

  return(p);
}


// Given a grid cell and an isolevel, calculate the triangular
  // facets required to represent the isosurface through the cell.
   //Return the number of triangular facets.
   //`triangles' will be loaded up with the vertices at most 5 triangular facets.
   //0 will be returned if the grid cell is either totally above
   //of totally below the isolevel.

  //By Paul Bourke <pbourke@swin.edu.au>

static int
march_one_cube (GRIDCELL grid, double isolevel, TRIANGLE *triangles)
{
  int i, ntriang;
  int cubeindex;
  XYZ vertlist[12];

//    Determine the index into the edge table which
  //  tells us which vertices are inside of the surface
  cubeindex = 0;
  if (grid.val[0] < isolevel) cubeindex |= 1;
  if (grid.val[1] < isolevel) cubeindex |= 2;
  if (grid.val[2] < isolevel) cubeindex |= 4;
  if (grid.val[3] < isolevel) cubeindex |= 8;
  if (grid.val[4] < isolevel) cubeindex |= 16;
  if (grid.val[5] < isolevel) cubeindex |= 32;
  if (grid.val[6] < isolevel) cubeindex |= 64;
  if (grid.val[7] < isolevel) cubeindex |= 128;

  // Cube is entirely in/out of the surface 
  if (edgeTable[cubeindex] == 0)
    return(0);

  // Find the vertices where the surface intersects the cube 
  if (edgeTable[cubeindex] & 1)
    vertlist[0] =
      interp_vertex (isolevel,grid.p[0],grid.p[1],grid.val[0],grid.val[1]);
  if (edgeTable[cubeindex] & 2)
    vertlist[1] =
      interp_vertex (isolevel,grid.p[1],grid.p[2],grid.val[1],grid.val[2]);
  if (edgeTable[cubeindex] & 4)
    vertlist[2] =
      interp_vertex (isolevel,grid.p[2],grid.p[3],grid.val[2],grid.val[3]);
  if (edgeTable[cubeindex] & 8)
    vertlist[3] =
      interp_vertex (isolevel,grid.p[3],grid.p[0],grid.val[3],grid.val[0]);
  if (edgeTable[cubeindex] & 16)
    vertlist[4] =
      interp_vertex (isolevel,grid.p[4],grid.p[5],grid.val[4],grid.val[5]);
  if (edgeTable[cubeindex] & 32)
    vertlist[5] =
      interp_vertex (isolevel,grid.p[5],grid.p[6],grid.val[5],grid.val[6]);
  if (edgeTable[cubeindex] & 64)
    vertlist[6] =
      interp_vertex (isolevel,grid.p[6],grid.p[7],grid.val[6],grid.val[7]);
  if (edgeTable[cubeindex] & 128)
    vertlist[7] =
      interp_vertex (isolevel,grid.p[7],grid.p[4],grid.val[7],grid.val[4]);
  if (edgeTable[cubeindex] & 256)
    vertlist[8] =
      interp_vertex (isolevel,grid.p[0],grid.p[4],grid.val[0],grid.val[4]);
  if (edgeTable[cubeindex] & 512)
    vertlist[9] =
      interp_vertex (isolevel,grid.p[1],grid.p[5],grid.val[1],grid.val[5]);
  if (edgeTable[cubeindex] & 1024)
    vertlist[10] =
      interp_vertex (isolevel,grid.p[2],grid.p[6],grid.val[2],grid.val[6]);
  if (edgeTable[cubeindex] & 2048)
    vertlist[11] =
      interp_vertex (isolevel,grid.p[3],grid.p[7],grid.val[3],grid.val[7]);

// Create the triangle
  ntriang = 0;
  for (i=0; triTable[cubeindex][i] != -1; i+=3)
    {
      triangles[ntriang].p[0] = vertlist[triTable[cubeindex][i  ]];
      triangles[ntriang].p[1] = vertlist[triTable[cubeindex][i+1]];
      triangles[ntriang].p[2] = vertlist[triTable[cubeindex][i+2]];
      ntriang++;
    }

  return(ntriang);
}


// Walking the grid.  By jwz.


// Computes the normal of the scalar field at the given point,
  // for vertex normals (as opposed to face normals.)
 
static void
do_function_normal (double x, double y, double z,
                    double (*compute_fn) (double x, double y, double z,
                                          void *closure),
                    void *c)
{
  XYZ n;
  double off = 0.5;
  n.x = compute_fn (x-off, y, z, c) - compute_fn (x+off, y, z, c);
  n.y = compute_fn (x, y-off, z, c) - compute_fn (x, y+off, z, c);
  n.z = compute_fn (x, y, z-off, c) - compute_fn (x, y, z+off, c);
// normalize (&n); 
  glNormal3f (n.x, n.y, n.z);
}


// Given a function capable of generating a value at any XYZ position,
 //  creates OpenGL faces for the solids defined.

  // init_fn is called at the beginning for initial, and returns an object.
   //free_fn is called at the end.

   //compute_fn is called for each XYZ in the specified grid, and returns
   //the double value of that coordinate.  If smoothing is on, then
   //compute_fn will also be called twice more for each emitted vertex,
   //in order to calculate vertex normals (so don't assume it will only
   //be called with values falling on the grid boundaries.)

   //Points are inside an object if the are less than `isolevel', and
   //outside otherwise.
void
marching_cubes (int grid_size,     // density of the mesh 
                double isolevel,   // cutoff point for "in" versus "out" 
                int wireframe_p,   // wireframe, or solid 
                int smooth_p,      // smooth, or faceted 

                void * (*init_fn)    (double grid_size, void *closure1),
                double (*compute_fn) (double x, double y, double z,
                                      void *closure2),
                void   (*free_fn)    (void *closure2),
                void *closure1,

                unsigned long *polygon_count)
{
  int planesize = grid_size * grid_size;
  int x, y, z;
  void *closure2 = 0;
  unsigned long polys = 0;
  double *layers;

  layers = (double *) calloc (sizeof (*layers), planesize * 2);
  if (!layers)
    {
      fprintf (stderr, "%s: out of memory for %dx%dx%d grid\n",
               progname, grid_size, grid_size, 2);
      exit (1);
    }

  if (init_fn)
    closure2 = init_fn (grid_size, closure1);

  glFrontFace(GL_CCW);
  if (!wireframe_p)
    glBegin (GL_TRIANGLES);

  for (z = 0; z < grid_size; z++)
    {
      double *layer0 = (z & 1 ? layers+planesize : layers);
      double *layer1 = (z & 1 ? layers           : layers+planesize);
      double *row;

// Fill in the XY grid on the currently-bottommost layer.
      row = layer1;
      for (y = 0; y < grid_size; y++, row += grid_size)
        {
          double *cell = row;
          for (x = 0; x < grid_size; x++, cell++)
            *cell = compute_fn (x, y, z, closure2);
        }

      // Now we've completed one layer (an XY slice of Z.)  Now we can
      //   generate the polygons that fill the space between this layer
      //   and the previous one (unless this is the first layer.)
      
      if (z == 0) continue;

      for (y = 1; y < grid_size; y += 1)
        for (x = 1; x < grid_size; x += 1)
          {
            TRIANGLE tri[6];
            int i, ntri;
            GRIDCELL cell;

            // This is kinda hokey, there ought to be a more efficient
              // way to do this... 
            cell.p[0].x = x-1; cell.p[0].y = y-1; cell.p[0].z = z-1;
            cell.p[1].x = x  ; cell.p[1].y = y-1; cell.p[1].z = z-1;
            cell.p[2].x = x  ; cell.p[2].y = y  ; cell.p[2].z = z-1;
            cell.p[3].x = x-1; cell.p[3].y = y  ; cell.p[3].z = z-1;
            cell.p[4].x = x-1; cell.p[4].y = y-1; cell.p[4].z = z  ;
            cell.p[5].x = x  ; cell.p[5].y = y-1; cell.p[5].z = z  ;
            cell.p[6].x = x  ; cell.p[6].y = y  ; cell.p[6].z = z  ;
            cell.p[7].x = x-1; cell.p[7].y = y  ; cell.p[7].z = z  ;

# define GRID(X,Y,WHICH) ((WHICH) \
                          ? layer1[((Y)*grid_size) + ((X))] \
                          : layer0[((Y)*grid_size) + ((X))])

            cell.val[0] = GRID (x-1, y-1, 0);
            cell.val[1] = GRID (x  , y-1, 0);
            cell.val[2] = GRID (x  , y  , 0);
            cell.val[3] = GRID (x-1, y  , 0);
            cell.val[4] = GRID (x-1, y-1, 1);
            cell.val[5] = GRID (x  , y-1, 1);
            cell.val[6] = GRID (x  , y  , 1);
            cell.val[7] = GRID (x-1, y  , 1);
# undef GRID

            // Now generate the triangles for this cubic segment,
              // and emit the GL faces.
           //
            ntri = march_one_cube (cell, isolevel, tri);
            polys += ntri;
            for (i = 0; i < ntri; i++)
              {
                if (wireframe_p) glBegin (GL_LINE_LOOP);

               // If we're smoothing, we need to call the field function
                 //  again for each vertex (via function_normal().)  If we're
                  // not smoothing, then we can just compute the normal from
                  // this triangle.
                 
                if (!smooth_p)
                  do_normal (tri[i].p[0].x, tri[i].p[0].y, tri[i].p[0].z,
                             tri[i].p[1].x, tri[i].p[1].y, tri[i].p[1].z,
                             tri[i].p[2].x, tri[i].p[2].y, tri[i].p[2].z);

# define VERT(X,Y,Z) \
                if (smooth_p) \
                  do_function_normal ((X), (Y), (Z), compute_fn, closure2); \
                glVertex3f ((X), (Y), (Z))

                VERT (tri[i].p[0].x, tri[i].p[0].y, tri[i].p[0].z);
                VERT (tri[i].p[1].x, tri[i].p[1].y, tri[i].p[1].z);
                VERT (tri[i].p[2].x, tri[i].p[2].y, tri[i].p[2].z);
# undef VERT
                if (wireframe_p) glEnd ();
              }
          }
    }

  if (!wireframe_p)
    glEnd ();

  free (layers);

  if (free_fn)
    free_fn (closure2);

  if (polygon_count)
    *polygon_count = polys;
}
*/

