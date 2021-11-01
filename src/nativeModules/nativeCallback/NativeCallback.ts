import {
  EventSubscription,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';

const {NativeCallback} = NativeModules;

interface AndroidLifecycleHooks {
  /**
   * The system calls this method as the first indication that the user
   * is leaving your activity (though it does not always mean the activity
   * is being destroyed); it indicates that the activity is no longer in the
   * foreground (though it may still be visible if the user is in multi-window mode).
   * Use the onPause() method to pause or adjust operations that should
   * not continue (or should continue in moderation) while the Activity
   * is in the Paused state, and that you expect to resume shortly.
   */
  onHostPause(): void;

  onHostResume(): void;

  onHostDestroy(): void;
}

export default class AndroidLifeCycle {
  private readonly paused: string = 'onHostPause';
  private readonly resumed: string = 'onHostResume';
  private readonly destroyed: string = 'onHostDestroy';
  hostPausedListener: EventSubscription | null;
  hostResumedListener: EventSubscription | null;
  hostDestroyedListener: EventSubscription | null;

  constructor() {
    this.hostPausedListener = null;
    this.hostResumedListener = null;
    this.hostDestroyedListener = null;
  }

  registerCallback(callback: AndroidLifecycleHooks) {
    const {paused, resumed, destroyed} = this;
    let eventEmitter = new NativeEventEmitter(NativeCallback);
    this.hostPausedListener = eventEmitter.addListener(
      paused,
      callback.onHostPause!,
    );

    this.hostPausedListener = eventEmitter.addListener(
      resumed,
      callback.onHostResume!,
    );

    this.hostDestroyedListener = eventEmitter.addListener(
      destroyed,
      callback.onHostDestroy!,
    );
  }

  cancelCallback() {
    this.hostPausedListener?.remove();
  }
}
