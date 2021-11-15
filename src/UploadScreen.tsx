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
  StyleSheet,
  Text,
  ToastAndroid,
  View,
} from 'react-native';
import VideoUploader from './nativeModules/workManager/VideoUploader';
import {launchImageLibrary} from 'react-native-image-picker';

interface IAppState {
  uris: string[];
  isUploading: boolean;
  uploadProgress: 0.0;
}
class App extends React.Component {
  state: IAppState;
  private eventListener: EmitterSubscription | null;
  constructor(props: any) {
    super(props);
    this.eventListener = null;
    this.state = {
      uris: [],
      isUploading: false,
      uploadProgress: 0.0,
    };
  }
  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(NativeModules.WorkManager);
    this.eventListener = eventEmitter.addListener('video_progress', event => {
      console.log(event);
      this.setState({uploadProgress: event.progress});
      if (event.progress === 100.0) {
        this.setState({isUploading: false});
      }
    });
  }

  componentWillUnmount() {
    this.eventListener?.remove(); //Removes the listener
  }

  enqueueUpload(videoUrls: string[]) {
    VideoUploader.enqueueVideoUploads(videoUrls);
    this.setState({isUploading: true});
  }

  render() {
    const {uris, isUploading, uploadProgress} = this.state;
    return (
      <>
        <View style={styles.column}>
          <View style={styles.button}>
            <Button
              disabled={isUploading}
              title={'Enqueue Work'}
              onPress={() => {
                if (uris.length > 0) {
                  this.enqueueUpload(uris);
                } else {
                  ToastAndroid.show('Pass some uris', 1000);
                }
              }}
            />
          </View>
          <View style={styles.button}>
            <Button
              title={'Select Media'}
              onPress={() => {
                launchImageLibrary(
                  {mediaType: 'video', selectionLimit: 20},
                  response => {
                    const uri = response.assets?.map(it => it.uri);
                    this.setState({uris: uri});
                  },
                );
              }}
            />
          </View>
        </View>
        <View style={styles.column}>
          {!isUploading ? (
            <Text>{JSON.stringify(uris)}</Text>
          ) : (
            <Text>UploadProgress: {uploadProgress}</Text>
          )}
        </View>
      </>
    );
  }
}

const styles = StyleSheet.create({
  column: {
    flex: 1,
    justifyContent: 'center',
    flexDirection: 'row',
  },
  button: {
    marginVertical: 10,
    marginHorizontal: 20,
  },
});

export default App;
