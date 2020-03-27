/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

/**
 * This interface provides the means to identify objects that wish to be
 * notified of changes to a {@link CameraConfiguration}.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @deprecated Unneeded, since CameraConfiguration is not needed anymore.
 * @see CameraConfiguration
 */
@Deprecated
public interface CameraListenerInterface {

    /**
     * Notifies this object that the Relation has changed. Implementations should
     * then fetch the changes, for example with
     * {@link CameraConfiguration#getCenter() getCenter()},
     * {@link CameraConfiguration#getZoom() getZoom()},
     * {@link CameraConfiguration#getHeight() getHeight()} or
     * {@link CameraConfiguration#getWidth() getWidth()}
     *
     * @see CameraConfiguration
     * @see CameraConfiguration#addListener
     * @see CameraConfiguration#changed
     */
    public void onCameraConfigurationChange();
}
