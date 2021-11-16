import React, {Component} from 'react';
import {Button, StyleSheet, Text, View} from 'react-native';
import CallbackPromise from './nativeModules/callback/CallbackPromise';

class CallbackAndPromise extends Component {
  doSomeWork() {
    CallbackPromise.doWork('test', something => {
      console.log(something || 'some - thing');
    })
      .then(res => {
        console.log(res);
      })
      .catch(err => console.log(err));
    // CallbackPromise.doWork('Hello world', () => {
    //   console.log('hello world');
    // })
    //   .then(res => {
    //     ToastAndroid.show(JSON.stringify(res), 500);
    //   })
    //   .catch(err => console.log(err.message));
  }
  render() {
    return (
      <View style={styles.row}>
        <Text>Hello world</Text>
        <Button
          onPress={() => {
            this.doSomeWork();
          }}
          title={'Hello World'}
        />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  row: {
    justifyContent: 'center',
    backgroundColor: 'red',
  },
});

export default CallbackAndPromise;
