package org.mbari.m3.corelib.services.varsuserserver.v1;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.mbari.m3.corelib.Initializer;
import org.mbari.m3.corelib.model.PreferenceNode;

import static org.mbari.m3.corelib.util.AsyncUtils.await;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.typesafe.config.ConfigFactory;



/**
 * @author Brian Schlining
 * @since 2017-06-27T09:28:00
 */
public class KBPrefServiceTest {

    Random random = new Random();
    KBPrefService prefService = Initializer.getInjector().getInstance(KBPrefService.class);
    Duration timeout = ConfigFactory.load().getDuration("preferences.service.timeout");
    PreferenceNode node = new PreferenceNode("/brian/trashme/trash/" + random.nextInt(), "Foo", "Bar");

    @Test
    public void testCrud() {
        // Create
        CompletableFuture<PreferenceNode> f0 = prefService.create(node);
        Optional<PreferenceNode> nodeOpt = await(f0, timeout);
        assertTrue("No preference value was returned", nodeOpt.isPresent());
        PreferenceNode pn0 = nodeOpt.get();
        assertEquals("Wrong name returned after update", node.getName(), pn0.getName());
        assertEquals("Wrong key returned after update", node.getKey(), pn0.getKey());
        assertEquals("Wrong name returned after update", node.getValue(), pn0.getValue());

        // Find
        Optional<List<PreferenceNode>> nodeOpt0 = await(prefService.findByName(node.getName()), timeout);
        assertTrue("Failed to find node by name", nodeOpt0.isPresent());

        String n = node.getName();
        Optional<List<PreferenceNode>> nodeOpt1 = await(prefService.findByNameLike(n.substring(0, n.length() - 2)), timeout);
        assertTrue("Failed to find node by name like", nodeOpt1.isPresent());
        assertTrue("Failed to find node by name like", nodeOpt1.get().size() == 1);

        Optional<Optional<PreferenceNode>> nodeOpt2 = await(prefService.findByNameAndKey(node.getName(), node.getKey()), timeout);
        assertTrue("Failed to find node by name and key", nodeOpt2.isPresent());
        assertTrue("Failed to find node by name and key", nodeOpt2.get().isPresent());

        // Update
        PreferenceNode newNode = new PreferenceNode(node.getName(), node.getKey(), "Orange");
        await(prefService.update(newNode), timeout);
        Optional<Optional<PreferenceNode>> nodeOpt3 = await(prefService.findByNameAndKey(node.getName(), node.getKey()), timeout);
        assertTrue("Failed to find node by name and key", nodeOpt3.isPresent());
        assertTrue("Failed to find node by name and key", nodeOpt3.get().isPresent());
        PreferenceNode pn2 = nodeOpt3.get().get();
        assertEquals("Wrong name returned after update", newNode.getName(), pn2.getName());
        assertEquals("Wrong key returned after update", newNode.getKey(), pn2.getKey());
        assertEquals("Wrong value returned after update", newNode.getValue() ,pn2.getValue());

        // Delete

        await(prefService.delete(node), timeout);
        Optional<Optional<PreferenceNode>> nodeOpt4 = await(prefService.findByNameAndKey(node.getName(), node.getKey()), timeout);
        assertTrue("Failed to delete node", nodeOpt4.isPresent());
        assertFalse("Failed to delete node", nodeOpt4.get().isPresent());
    }



    @Test
    @Ignore
    public void testDelete() {
        Optional<Optional<PreferenceNode>> nodeOpt0 = await(prefService.findByNameAndKey(node.getName(), node.getKey()), timeout);
        assertTrue("Failed to delete node", nodeOpt0.isPresent());
        assertTrue("Failed to delete node", nodeOpt0.get().isPresent());
        await(prefService.delete(node), timeout);
        Optional<Optional<PreferenceNode>> nodeOpt1 = await(prefService.findByNameAndKey(node.getName(), node.getKey()), timeout);
        assertTrue("Failed to delete node", nodeOpt1.isPresent());
        assertFalse("Failed to delete node", nodeOpt1.get().isPresent());
    }

}
