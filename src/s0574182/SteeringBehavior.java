package s0574182;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;

import lenz.htw.ai4g.ai.AI;
import lenz.htw.ai4g.ai.DivingAction;
import lenz.htw.ai4g.ai.Info;
import lenz.htw.ai4g.ai.PlayerAction;

public class SteeringBehavior extends AI {


    public SteeringBehavior(Info info) {
        super(info);
        enlistForTournament(574182, 573048);
    }

    @Override
    public Color getColor() {
        return Color.PINK;
    }

    public String getName() {
        return "Magdalena";
    }

    int scoreBeimLetztenAufrufVonUpdate;
    int lastX, lastY;
    Point turn;
    boolean obstacle;
    ArrayList<Integer> foundPearls = new ArrayList<>();
    int oldScore;
    //distance to nearest pearl
    private float getDistance(Point p1, Point p2) {
        return (float) Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }

    @Override
    public PlayerAction update() {
        float power = 0.5f;
        //double up = (Math.PI / 2);
        float maxVel = info.getMaxVelocity();    //maximale Geschwindigkeit
        float vel = info.getVelocity();    //aktuelle Geschwindigkeit
        int x = info.getX();    //meine Position
        int y = info.getY();

        //info.getAir();
        info.getScore();
        float orientation = info.getOrientation();

        Point[] pearl = info.getScene().getPearl();
        //sorting the pearls by X coordinate
        Arrays.sort(pearl, new Comparator<Point>() {
            public int compare(Point p1, Point p2) {
                return Integer.compare(p1.x, p2.x);
            }
        });

        //first pearl
        Point nearestPoint = pearl[0];

        if (oldScore < info.getScore()) {
            float counter = Integer.MAX_VALUE;
            //loop for checking whether we already collected a pearl, if not - add to array list
            for (int i = 0; i < pearl.length; i++) {
                //checks the distance from the swimmer to the nearest pearl
                if (getDistance(nearestPoint, pearl[i]) < counter) {
                    counter = getDistance(new Point(x, y), pearl[i]);
                }
                foundPearls.add(i);
            }
        }


        if (info.getScore() != scoreBeimLetztenAufrufVonUpdate)
            scoreBeimLetztenAufrufVonUpdate = info.getScore();


        //check near swimmer, no obstacle
        if (obstacle && x > x - 10 && x < turn.x + 10 && y > turn.y - 10 && y < turn.y + 10)
            obstacle = false;

        Path2D[] obstacles = info.getScene().getObstacles();
        for (int i = 0; i < obstacles.length; i++) {
            //obstacle under the swimmer
            if (obstacles[i].contains(x, y - 5)) {
                turn = new Point(x - 15, y + 20);
                obstacle = true;
            }
            //obstacle over the swimmer
            if (obstacles[i].contains(x, y + 5)) {
                turn = new Point(x - 20, y - 20);
                obstacle = true;
            }
            //obstacle to the right of the swimmer
            if (obstacles[i].contains(x + 5, y)) {
                turn = new Point(x - 20, y + 20);
                obstacle = true;
            }
            //obstacle to the left of the swimmer
            if (obstacles[i].contains(x - 5, y)) {
                turn = new Point(x, y + 20);
                obstacle = true;
            }
        }


           if(!obstacle) {
                float directionX = pearl[info.getScore()].x - x;
                float directionY = pearl[info.getScore()].y - y;
                return new DivingAction(power, (float) Math.atan2(directionY, directionX));
            } else {
                float directionX = turn.x - x;
                float directionY = turn.y - y;
                return new DivingAction(power, (float) Math.atan2(directionY, directionX));
            }
        }
    }

