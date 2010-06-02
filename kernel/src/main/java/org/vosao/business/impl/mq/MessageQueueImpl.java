/**
 * Vosao CMS. Simple CMS for Google App Engine.
 * Copyright (C) 2009 Vosao development team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * email: vosao.dev@gmail.com
 */

package org.vosao.business.impl.mq;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vosao.business.mq.Message;
import org.vosao.business.mq.MessageQueue;
import org.vosao.business.mq.TopicSubscriber;
import org.vosao.common.VosaoContext;
import org.vosao.global.SystemService;
import org.vosao.servlet.MessageQueueTaskServlet;
import org.vosao.utils.StreamUtil;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.repackaged.com.google.common.util.Base64;

/**
 * 
 * @author Alexander Oleynik
 *
 */
public class MessageQueueImpl implements MessageQueue {

	private Map<String, List<TopicSubscriber>> subscribers = 
		new HashMap<String, List<TopicSubscriber>>();
	
	private SystemService getSystemService() {
		return VosaoContext.getInstance().getBusiness().getSystemService();
	}
	
	@Override
	public void publish(Message message) {
		Queue queue = getSystemService().getQueue("import");
		queue.add(url(MessageQueueTaskServlet.MQ_URL)
				.param("topic", message.getTopic())
				.param("message", Base64.encode(StreamUtil.toBytes(message))));
	}

	@Override
	public void subscribe(String topic, TopicSubscriber subscriber) {
		if (!subscribers.containsKey(topic)) {
			subscribers.put(topic, new ArrayList<TopicSubscriber>());
		}
		if (!subscribers.get(topic).contains(subscriber)) {
			subscribers.get(topic).add(subscriber);
		}
	}

	@Override
	public void unsubscribe(String topic, TopicSubscriber subscriber) {
		if (subscribers.containsKey(topic)) {
			if (subscribers.get(topic).contains(subscriber)) {
				subscribers.get(topic).remove(subscriber);
			}
		}
	}

	@Override
	public List<TopicSubscriber> getSubscribers(String topic) {
		if (subscribers.containsKey(topic)) {
			return Collections.unmodifiableList(subscribers.get(topic));
		}
		return Collections.EMPTY_LIST;
	}

}