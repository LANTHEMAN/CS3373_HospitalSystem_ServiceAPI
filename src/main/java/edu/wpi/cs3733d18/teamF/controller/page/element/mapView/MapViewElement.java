package edu.wpi.cs3733d18.teamF.controller.page.element.mapView;

import edu.wpi.cs3733d18.teamF.ImageCacheSingleton;
import edu.wpi.cs3733d18.teamF.Map;
import edu.wpi.cs3733d18.teamF.controller.PermissionSingleton;
import edu.wpi.cs3733d18.teamF.controller.page.PageElement;
import edu.wpi.cs3733d18.teamF.gfx.impl.UglyMapDrawer;
import edu.wpi.cs3733d18.teamF.graph.Node;
import edu.wpi.cs3733d18.teamF.graph.Path;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import net.kurobako.gesturefx.GesturePane;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;


public class MapViewElement extends PageElement {
    // used to see if the floor has changed to update the map drawn
    MapListener mapListener;
    @FXML
    private GesturePane gesturePane;
    @FXML
    private ImageView mapImage;
    @FXML
    private Pane mapContainer;
    @FXML
    private AnchorPane root;
    private Node selectedNodeStart = null;
    private Node selectedNodeEnd = null;
    private Node modifyNode = null;
    private boolean ctrlHeld = false;
    private boolean draggingNode;
    private Node heldNode;
    private boolean nodesShown = false;
    private PaneMapController mapDrawController;
    private String mapFloorDrawn;
    private boolean isMap2D;

    private Map map;

    private MapViewListener listener;

    public void initialize(MapViewListener listener, Map map, AnchorPane sourcePane) {
        // initialize fundamentals
        this.listener = listener;
        this.map = map;
        map.addObserver(mapListener = new MapListener());
        mapFloorDrawn = map.getFloor();
        isMap2D = map.is2D();
        initElement(sourcePane, root);

        mapDrawController = new PaneMapController(mapContainer, map, new UglyMapDrawer());
        mapDrawController.showEdges();
        mapDrawController.showNodes();

        refreshFloorDrawn();

        // set default zoom
        gesturePane.zoomTo(2, new Point2D(600, 600));

        mapContainer.setOnMouseReleased(e -> {
            if (!PermissionSingleton.getInstance().isAdmin()) {
                return;
            }

            double map_x = 5000;
            double map_y = 3400;
            double map3D_y = 2772;
            if (!map.is2D()) {
                map_y = map3D_y;
            }

            Point2D mapPos = new Point2D(e.getX() * map_x / mapContainer.getMaxWidth()
                    , e.getY() * map_y / mapContainer.getMaxHeight());

            HashSet<Node> nodes = map.getNodes(node -> new Point2D(node.getPosition().getX()
                    , node.getPosition().getY()).distance(mapPos) < 8 && node.getFloor().equals(map.getFloor())
            );

            if (nodes.size() > 0) {
                // TODO get closest node
                Node node = nodes.iterator().next();
                mapDrawController.selectNode(node);


                if (e.getButton() == MouseButton.PRIMARY) {
                    map.addEdge(node, selectedNodeEnd);
                }

                if (e.getButton() == MouseButton.SECONDARY) {
                    map.removeEdge(node, selectedNodeEnd);
                }
            }

        });

        mapContainer.setOnMouseClicked(e -> {
            double map_x = 5000;
            double map_y = 3400;
            double map3D_y = 2772;
            if (!map.is2D()) {
                map_y = map3D_y;
            }

            Point2D mapPos = new Point2D(e.getX() * map_x / mapContainer.getMaxWidth()
                    , e.getY() * map_y / mapContainer.getMaxHeight());

            // if editing maps
            HashSet<Node> nodes = new HashSet<>();
            if (PermissionSingleton.getInstance().isAdmin()) {
                if (map.is2D()) {
                    nodes = map.getNodes(node -> new Point2D(node.getPosition().getX()
                            , node.getPosition().getY()).distance(mapPos) < 8 && node.getFloor().equals(map.getFloor()));
                } else {
                    nodes = map.getNodes(node -> new Point2D(node.getWireframePosition().getX()
                            , node.getWireframePosition().getY()).distance(mapPos) < 8 && node.getFloor().equals(map.getFloor()));
                }
            }
            // not editing mapView
            else {
                nodes.add(map.findNodeClosestTo(mapPos.getX(), mapPos.getY(), map.is2D(), node -> node.getFloor().equals(map.getFloor())));
            }
            if (nodes.size() > 0 && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                if (!nodesShown) {

                    Node src = map.findNodeClosestTo(mapPos.getX(), mapPos.getY(), map.is2D(), node -> node.getFloor().equals(map.getFloor()));
                    if ((map.is2D() ? mapPos.distance(src.getPosition()) : mapPos.distance(src.getWireframePosition())) < 200) {
                        Path path = map.getPath(map.findNodeClosestTo(selectedNodeStart.getPosition().getX(), selectedNodeStart.getPosition().getY(), true), src);
                        mapDrawController.showPath(path);
                    } else {
                        return;
                    }
                }

                Node node = map.findNodeClosestTo(mapPos.getX(), mapPos.getY(), map.is2D(), node1 -> node1.getFloor().equals(map.getFloor()));
                if (nodesShown) {
                    mapDrawController.selectNode(node);
                }
                selectedNodeEnd = node;


                if (map.is2D()) {

                } else {

                }


                // TODO implement change building, change type

                modifyNode = node;

            }

            if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 1) {
                if (nodes.size() > 0 && !nodesShown) {
                    selectedNodeStart = nodes.iterator().next();
                    Path path = map.getPath(selectedNodeStart, selectedNodeEnd);
                    if (path.getNodes().size() < 2) {
                        if (map.getNeighbors(selectedNodeStart).size() > 0) {
                            path = map.getPath(selectedNodeStart, map.getNeighbors(selectedNodeStart).iterator().next());
                        }
                    }
                    mapDrawController.showPath(path);
                }
            }

            // remove a node
            if (e.getButton() == MouseButton.SECONDARY && e.getClickCount() == 2) {
                if (!map.is2D()) {
                    return;
                }

                // td fixed bug - deleting end node on double right click
                // added && nodesShown
                if (nodes.size() > 0 && nodesShown) {
                    // TODO get closest node
                    //selectedNodeStart = mapView.findNodeClosestTo(1850, 1035, true, node -> node.getFloor().equals(mapView.getFloor()));
                    //sourceLocation.setText(selectedNodeStart.getLongName());
                    mapDrawController.unshowPath();
                    map.removeNode(selectedNodeEnd);
                    selectedNodeEnd = null;
                }
            }
            // create a new node
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                if (!map.is2D() || !PermissionSingleton.getInstance().isAdmin()) {
                    return;
                }
                selectedNodeEnd = null;
            }
        });

        mapContainer.setOnDragDetected(e -> {
            double map_x = 5000;
            double map_y = 3400;
            double map3D_y = 2772;
            if (!map.is2D()) {
                map_y = map3D_y;
            }

            Point2D mapPos = new Point2D(e.getX() * map_x / mapContainer.getMaxWidth()
                    , e.getY() * map_y / mapContainer.getMaxHeight());


            if (PermissionSingleton.getInstance().isAdmin()) {
                Node node = map.findNodeClosestTo(mapPos.getX(), mapPos.getY(), map.is2D());
                double deltaX = mapPos.getX() - node.getPosition().getX();
                double deltaY = mapPos.getY() - node.getPosition().getY();
                draggingNode = false;
                if (Math.sqrt((deltaX * deltaX) + (deltaY * deltaY)) < 20) {
                    draggingNode = true;
                    heldNode = node;
                }
            }
        });

        mapContainer.setOnMouseDragged(e -> {
            double map_x = 5000;
            double map_y = 3400;
            double map3D_y = 2772;
            if (!map.is2D()) {
                map_y = map3D_y;
            }

            Point2D mapPos = new Point2D(e.getX() * map_x / mapContainer.getMaxWidth()
                    , e.getY() * map_y / mapContainer.getMaxHeight());

            if (PermissionSingleton.getInstance().isAdmin() && nodesShown) {
                if (draggingNode) {
                    heldNode.setPosition(mapPos);
                }
            }
        });
    }


    private void refreshFloorDrawn() {
        mapFloorDrawn = map.getFloor();
        isMap2D = map.is2D();
        int index;
        if (map.getFloor().equals("L2")) {
            index = 0;
        } else if (map.getFloor().equals("L1")) {
            index = 1;
        } else if (map.getFloor().equals("0G")) {
            index = 2;
        } else if (map.getFloor().equals("01")) {
            index = 3;
        } else if (map.getFloor().equals("02")) {
            index = 4;
        } else {
            index = 5;
        }
        if (map.is2D()) {
            mapImage.setImage(ImageCacheSingleton.maps2D[index]);
        } else {
            mapImage.setImage(ImageCacheSingleton.maps3D[index]);
        }
    }

    /**
     * Just used to see if the map floor image has to be changed
     */
    private class MapListener implements Observer {
        @Override
        public void update(Observable o, Object arg) {
            if (!mapFloorDrawn.equals(map.getFloor()) || isMap2D != map.is2D()) {
                refreshFloorDrawn();
            }
        }
    }
}
