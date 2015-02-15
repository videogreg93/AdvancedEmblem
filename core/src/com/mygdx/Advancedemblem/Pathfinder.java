/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.Advancedemblem;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.mygdx.Advancedemblem.MyGdxGame.Unit;
import java.util.*;

public class Pathfinder {

    ArrayList openList;
    ArrayList closedList;
    ArrayList allMoves;
    TiledMap map;
    TiledMapTileLayer layer;

    public class Node {

        Node parent;
        float x;
        float y;
        int costToGetHere;

        Node(Node parent, float x, float y, int cost) {
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.costToGetHere = cost;
        }
    }

    Pathfinder(TiledMap tiledMap) {
        openList = new ArrayList();
        closedList = new ArrayList();
        this.map = tiledMap;
        layer = (TiledMapTileLayer) map.getLayers().get(0);
    }

    public ArrayList getPossibleMoves(Unit unit) {
        this.clear();
        Node arrival = new Node(null, unit.getX(), unit.getY(), 0);
        openList.add(arrival);
        Node currentNode;
        while (!openList.isEmpty()) {
            currentNode = (Node) openList.get(openList.size() - 1);
            for (int x = -1; x <= 1; x += 2) {
                checkTile(unit, currentNode, x, 0);
                checkTile(unit, currentNode, 0, x);
            }
            openList.remove(currentNode);
            closedList.add(currentNode);
        }
        return allMoves;
    }

    private boolean isReachable(Node arrival, Unit unit, Cell cell, int x, int y) {
       
        if (x < 0 || y < 0 )
            return false;
        else
            return unit.maxMoves - arrival.costToGetHere - unit.getMovementCost(cell) >= 0;
    }

    private void clear() {
        openList.clear();
        closedList.clear();
        allMoves = new ArrayList();
    }

    private void checkTile(Unit unit, Node currentNode, int x, int y) {
        Cell cell = layer.getCell((int) currentNode.x/32 + x, (int) currentNode.y/32 + y);
        if (cell == null)
            System.out.print("Cell:" + (currentNode.x + x) + "," + (currentNode.y + y));
        if (isReachable(currentNode, unit, cell, (int) currentNode.x + x * 32, (int) currentNode.y + y * 32)) {
            Node temp = new Node(currentNode,
                    currentNode.x + x * 32,
                    currentNode.y + y * 32,
                    currentNode.costToGetHere + unit.getMovementCost(cell));
            // We now check if the array already contains it
            boolean contains = false;
            for (int i = 0; i < openList.size(); i++) {
                Node indexNode = (Node) openList.get(i);
                if (indexNode.x == temp.x && indexNode.y == temp.y) {
                    contains = true;
                }
            }
            for (int i = 0; i < closedList.size(); i++) {
                Node indexNode = (Node) closedList.get(i);
                if (indexNode.x == temp.x && indexNode.y == temp.y) {
                    contains = true;
                }
            }
            if (!contains) {
                openList.add(temp);
                allMoves.add(temp);
            }
        }
    }

}
