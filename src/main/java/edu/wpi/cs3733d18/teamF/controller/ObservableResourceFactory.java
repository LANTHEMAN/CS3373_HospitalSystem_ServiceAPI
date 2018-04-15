package edu.wpi.cs3733d18.teamF.controller;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Enumeration;
import java.util.ResourceBundle;

public class ObservableResourceFactory {

    private ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>(new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
            return null;
        }

        @Override
        public Enumeration<String> getKeys() {
            return null;
        }
    });

    public ObjectProperty<ResourceBundle> resourcesProperty() {
        return resources ;
    }
    public final ResourceBundle getResources() {
        return resourcesProperty().get();
    }
    public final void setResources(ResourceBundle resources) {
        resourcesProperty().set(resources);
    }

    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            { bind(resourcesProperty()); }
            @Override
            public String computeValue() {
                return getResources().getString(key);
            }
        };
    }
}