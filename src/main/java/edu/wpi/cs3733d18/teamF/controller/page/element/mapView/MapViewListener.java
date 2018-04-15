package edu.wpi.cs3733d18.teamF.controller.page.element.mapView;

import edu.wpi.cs3733d18.teamF.graph.Node;

public interface MapViewListener {
    void onSrcNodeSelected(Node node);
    void onDstNodeSelected(Node node);
}
