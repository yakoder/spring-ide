package org.springframework.ide.eclipse.boot.dash.docker.runtarget;

import org.springframework.ide.eclipse.boot.dash.api.App;
import org.springframework.ide.eclipse.boot.dash.model.RunState;

import com.spotify.docker.client.messages.Container;

public class DockerContainer implements App {

	private final Container container;

	public DockerContainer(Container container) {
		this.container = container;
	}

	@Override
	public String getName() {
		return getId();
	}

	@Override
	public String getId() {
		return container.id();
	}

	@Override
	public RunState fetchRunState() {
		return getRunState(container);
	}

	@Override
	public void setGoalState(RunState state) {
		// TODO Auto-generated method stub

	}
	
	public static RunState getRunState(Container container) {
		String state = container.state();
		if ("running".equals(state)) {
			return RunState.RUNNING;
		} else if ("exited".equals(state)) {
			return RunState.INACTIVE;
		}
		return RunState.UNKNOWN;
	}

}
