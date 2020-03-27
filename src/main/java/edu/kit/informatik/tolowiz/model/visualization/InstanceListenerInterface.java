/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

/**
 * This interface provides the means to identify objects that wish to be
 * informed of changes to a {@link InstanceConfiguration}.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see InstanceConfiguration
 */
public interface InstanceListenerInterface {

    /**
     * Notifies this object that the Instance has changed. Implementations should
     * then fatch the changes, for example with
     * {@link InstanceConfiguration#isVisible() isVisible()},
     * {@link InstanceConfiguration#getGroups() getGroups()}, or
     * {@link InstanceConfiguration#getIcon() getIcon()}.
     *
     * @see InstanceConfiguration
     * @see InstanceConfiguration#addListener
     * @see InstanceConfiguration#changed
     */
    public void onChange();
}
