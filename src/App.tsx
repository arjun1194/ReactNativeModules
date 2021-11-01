/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */

import React from 'react';
import {
  Button,
  EmitterSubscription,
  NativeEventEmitter,
  NativeModules,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  StyleSheet,
  Text,
  View,
} from 'react-native';
import AndroidLifeCycle from './nativeModules/nativeCallback/NativeCallback';
import Callback from './nativeModules/callback/Callback';

const {NativeCallback} = NativeModules;
interface IAppState {
  count: number;
  isSleeperActive: boolean;
}
class App extends React.Component {
  state: IAppState;
  private eventEmitter: AndroidLifeCycle;
  constructor(props: any) {
    super(props);
    this.eventEmitter = new AndroidLifeCycle();
    this.state = {
      count: 0,
      isSleeperActive: false,
    };
  }
  componentDidMount() {
    this.eventEmitter.registerCallback({
      onHostPause() {
        console.log('host paused');
      },
      onHostResume() {
        console.log('host resumed');
      },
      onHostDestroy() {
        console.log('host destroyed');
      },
    });
  }

  componentWillUnmount() {
    this.eventEmitter.cancelCallback();
  }

  render() {
    return (
      <View style={styles.row}>
        <View style={styles.column}>
          <Text>{this.state.count}</Text>

          <Button
            onPress={() => {
              this.setState({isSleeperActive: true});
              Callback.sleep(2, () => {
                console.log('its been 10 seconds');
              });
            }}
            title="Sleeper"
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  row: {flex: 1, justifyContent: 'center'},
  column: {flex: 1, justifyContent: 'center', alignItems: 'center'},
});

export default App;
