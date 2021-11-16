import React from 'react';
import {
  Button,
  EmitterSubscription,
  NativeEventEmitter,
  NativeModules,
  ScrollView,
  StyleSheet,
  Text,
  ToastAndroid,
  View,
} from 'react-native';
import VideoUploader from './nativeModules/workManager/VideoUploader';
import {launchImageLibrary} from 'react-native-image-picker';

interface IAppState {
  uris: string[];
  logs: string[];
  isUploading: boolean;
  uploadProgress: 0.0;
}
class UploadScreen extends React.Component {
  state: IAppState;
  private eventListener: EmitterSubscription | null;
  constructor(props: any) {
    super(props);
    this.eventListener = null;
    this.state = {
      logs: [],
      uris: [],
      isUploading: false,
      uploadProgress: 0.0,
    };
  }
  componentDidMount() {
    this.log('Starting Logs!');
    const eventEmitter = new NativeEventEmitter(NativeModules.WorkManager);
    this.eventListener = eventEmitter.addListener('video_progress', event => {
      this.clearLogs();
      this.log(`Progress --> ${event.progress}`);
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

  log(message: string) {
    this.state.logs.push(message);
  }

  render() {
    const {uris, logs} = this.state;
    return (
      <>
        <View style={{...styles.row, justifyContent: 'center'}}>
          <View style={styles.button}>
            <Button
              title={'Enqueue Work'}
              onPress={() => {
                if (uris.length > 0) {
                  this.log('Enqueue started');
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
                this.log('Opening Gallery Picker!');
                launchImageLibrary(
                  {mediaType: 'video', selectionLimit: 20},
                  response => {
                    const uri = response.assets?.map(it => it.uri);
                    const fileName = response.assets?.map(it => it.fileName);
                    this.log(`file picked  ---> ${fileName}`);
                    this.setState({uris: uri});
                  },
                );
              }}
            />
          </View>
        </View>
        <View style={{...styles.column, flex: 1, backgroundColor: '#fafafa'}}>
          <Text style={styles.h1}>Logs</Text>
          <ScrollView style={{marginTop: 10}}>
            {logs.map(log => (
              <View style={styles.row}>
                <Text style={{...styles.caption, paddingHorizontal: 10}}>
                  {new Date().toTimeString()}
                </Text>
                <Text style={{alignSelf: 'center', color: 'black'}}>{log}</Text>
              </View>
            ))}
          </ScrollView>
        </View>
      </>
    );
  }

  private clearLogs() {
    this.setState({logs: []});
  }
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    color: 'black',
  },
  column: {
    flexDirection: 'column',
  },
  button: {
    marginVertical: 10,
    marginHorizontal: 20,
  },

  h1: {
    fontSize: 20,
    fontWeight: 'bold',
  },

  caption: {
    fontWeight: '400',
    color: '#5a5a5a',
    fontSize: 12,
  },
});

export default UploadScreen;
