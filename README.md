# Javions Project
## Overview

Javions is a Java project aimed at facilitating air traffic control by decoding Automatic Dependent Surveillance-Broadcast (ADS-B) messages transmitted by aircraft and other airborne vehicles. These messages provide various information about the sender, such as its identity, position, speed, direction of movement, etc.

ADS-B messages are continuously broadcasted on the 1090 MHz frequency and can be captured either by a specialized receiver or by a Software-Defined Radio (SDR) connected to a computer running a program capable of decoding these messages.

The goal of this project, named Javions, is to develop a program capable of decoding ADS-B messages received by an SDR and displaying the aircraft that emitted them on a map. The graphical interface of the completed project is depicted below.

The SDR used for Javions is the AirSpy R2, as shown in the image below. Like any SDR, it must be connected to an antenna and a computer. Once tuned to a specific frequency, it digitizes the radio signal received from the antenna and then transmits it to the computer.

To receive ADS-B messages from an aircraft, no significant obstacles should obstruct the path between the aircraft and the receiving antenna. Therefore, positioning the antenna properly is crucial. The image in Figure 1, for example, was obtained with an antenna placed near the roof of a building in downtown Lausanne.

However, even under optimal reception conditions, the curvature of the Earth means that it's not possible to receive messages from aircraft located more than a few hundred kilometers away from the receiver. Thus, Javions can only display aircraft in the vicinity of Lausanne.

To cover a wider geographical area and enable long-distance aircraft tracking, it's possible to collect ADS-B messages received by a large number of radios distributed worldwide via the Internet. Several message collection sites, managed by aviation enthusiasts, researchers, activists, or commercial entities, perform this task. However, for simplicity, we won't interact with these sites as part of this project.
Organization

## Project Experience

Throughout the development of the Javions project, our team encountered various challenges and gained valuable insights into Java programming, software-defined radio technology, and project management. Here's a reflection on our journey:

### Overcoming Technical Hurdles

As we delved into the intricacies of decoding ADS-B messages and displaying aircraft positions on a map, we faced numerous technical hurdles. Understanding the structure and content of ADS-B messages proved to be particularly challenging, requiring in-depth research and experimentation. Additionally, integrating the functionality of the AirSpy R2 SDR into our Java application presented its own set of challenges, as we needed to ensure seamless communication between the hardware and software components.

### Learning from Mistakes

Throughout the project, we encountered bugs, errors, and unforeseen complications that tested our problem-solving skills. However, each setback provided us with an opportunity to learn and grow as developers. We were able to improve our debugging techniques, and developed a deeper understanding of Java's object-oriented principles.

### Enhancing Project Management Skills

Managing the Javions project required careful planning, coordination, and time management. From setting weekly goals and deadlines to dividing tasks and allocating resources. Moreover, regular communication and collaboration ensured that we stayed aligned with our objectives and made steady progress towards our goals.

### Conclusion

This was the biggest coding project that we had so far and we gained a lot knowledge. Working on it weekly and constantly improving our code was very fun and we are happy with our result.

