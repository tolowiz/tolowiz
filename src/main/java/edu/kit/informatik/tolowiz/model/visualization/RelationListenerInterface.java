/*
 * Copyright 2020 Anne Bernhart, Anja Hansen, Tobias Klumpp, Fabian Palitza,
 * Florian Patzer, Friedrich Volz, Sandra Wolf
 * SPDX-License-Identifier: Apache-2.0
 */
package edu.kit.informatik.tolowiz.model.visualization;

/**
 * This interface provides the means to identify objects that wish to be
 * informed of changes to a {@link RelationConfiguration}.
 *
 * @author Fabian Palitza
 * @version 1.0
 * @see RelationConfiguration
 */
public interface RelationListenerInterface {

    /**
     * Notifies this object that the Relation has changed. Implementations should
     * then fetch the changes, for example with
     * {@link RelationConfiguration#isVisible() isVisible()} or
     * {@link RelationConfiguration#getCurrentStyle() getCurrentStyle()}.
     *
     * @see RelationConfiguration
     * @see RelationConfiguration#addListener(RelationListenerInterface)
     * @see RelationConfiguration#changed()
     */
    public void onChange();
}
