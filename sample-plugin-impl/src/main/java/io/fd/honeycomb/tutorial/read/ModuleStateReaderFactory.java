/*
 * Copyright (c) 2016 Cisco and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fd.honeycomb.tutorial.read;

import static io.fd.honeycomb.tutorial.ModuleConfiguration.ELEMENT_SERVICE_NAME;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.fd.honeycomb.tutorial.CrudService;
import io.fd.honeycomb.translate.impl.read.GenericListReader;
import io.fd.honeycomb.translate.read.ReaderFactory;
import io.fd.honeycomb.translate.read.registry.ModifiableReaderRegistryBuilder;
import javax.annotation.Nonnull;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.sample.plugin.rev160918.SamplePluginState;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.sample.plugin.rev160918.SamplePluginStateBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.sample.plugin.rev160918.sample.plugin.params.Element;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

/**
 * Factory producing readers for sample-plugin plugin's data.
 */
public final class ModuleStateReaderFactory implements ReaderFactory {

    public static final InstanceIdentifier<SamplePluginState> ROOT_STATE_CONTAINER_ID =
            InstanceIdentifier.create(SamplePluginState.class);

    /**
     * Injected crud service to be passed to customizers instantiated in this factory.
     */
    @Inject
    @Named(ELEMENT_SERVICE_NAME)
    private CrudService<Element> crudService;

    @Override
    public void init(@Nonnull final ModifiableReaderRegistryBuilder registry) {

        // register reader that only delegate read's to its children
        registry.addStructuralReader(ROOT_STATE_CONTAINER_ID, SamplePluginStateBuilder.class);

        // just adds reader to the structure
        // use addAfter/addBefore if you want to add specific order to readers on the same level of tree
        // use subtreeAdd if you want to handle multiple nodes in single customizer/subtreeAddAfter/subtreeAddBefore if you also want to add order
        // be aware that instance identifier passes to subtreeAdd/subtreeAddAfter/subtreeAddBefore should define subtree,
        // therefore it should be relative from handled node down - InstanceIdentifier.create(HandledNode), not parent.child(HandledNode.class)
        registry.add(
                new GenericListReader<>(ROOT_STATE_CONTAINER_ID.child(Element.class),
                        new ElementStateCustomizer(crudService)));
    }
}
